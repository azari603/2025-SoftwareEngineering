package com.cheack.softwareengineering.service;

import com.cheack.softwareengineering.dto.*;
import com.cheack.softwareengineering.entity.Book;
import com.cheack.softwareengineering.entity.Profile;
import com.cheack.softwareengineering.entity.ReadingStatus;
import com.cheack.softwareengineering.entity.ReadingStatusType;
import com.cheack.softwareengineering.entity.Review;
import com.cheack.softwareengineering.repository.BookRepository;
import com.cheack.softwareengineering.repository.ProfileRepository;
import com.cheack.softwareengineering.repository.ReadingStatusRepository;
import com.cheack.softwareengineering.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StatsService {

    private final ReviewRepository reviewRepository;
    private final ReadingStatusRepository readingStatusRepository;
    private final BookRepository bookRepository;
    private final ProfileRepository profileRepository;

    /**
     * 전체 개요 통계
     */
    public StatsOverviewDto getOverview(Long userId) {
        long completed = readingStatusRepository.countByUserIdAndStatus(userId, ReadingStatusType.COMPLETED);
        long reading = readingStatusRepository.countByUserIdAndStatus(userId, ReadingStatusType.READING);
        long wishlist = readingStatusRepository.countByUserIdAndStatus(userId, ReadingStatusType.WISHLIST);

        Page<Review> allReviewsPage = reviewRepository.findByUserIdAndDeletedFalse(userId, Pageable.unpaged());
        List<Review> reviews = allReviewsPage.getContent();
        long reviewCount = allReviewsPage.getTotalElements();

        double avgRating = reviews.stream()
                .map(Review::getStarRating)
                .filter(Objects::nonNull)
                .mapToDouble(Double::doubleValue)
                .average()
                .orElse(0.0);

        return StatsOverviewDto.builder()
                .completedCount(completed)
                .readingCount(reading)
                .wishlistCount(wishlist)
                .reviewCount(reviewCount)
                .averageRating(avgRating)
                .build();
    }

    /**
     * 연도별 월간 시계열(완독 권수 기준)
     */
    public MonthlySeriesDto getMonthlySeries(Long userId, int year) {
        // COMPLETED 상태만 대상으로
        Page<ReadingStatus> page = readingStatusRepository
                .findByUserIdAndStatus(userId, ReadingStatusType.COMPLETED, Pageable.unpaged());

        long[] monthly = new long[12];

        page.getContent().forEach(rs -> {
            if (rs.getCreatedAt() == null) return;
            if (rs.getCreatedAt().getYear() != year) return;

            int month = rs.getCreatedAt().getMonthValue(); // 1 ~ 12
            monthly[month - 1]++;
        });

        List<Long> completedByMonth = Arrays.stream(monthly)
                .boxed()
                .collect(Collectors.toList());

        return MonthlySeriesDto.builder()
                .year(year)
                .completedByMonth(completedByMonth)
                .build();
    }

    /**
     * 연간 요약 통계
     */
    public YearlySummaryDto getYearlySummary(Long userId, int year) {
        // 완독 카운트
        Page<ReadingStatus> completedPage =
                readingStatusRepository.findByUserIdAndStatus(userId, ReadingStatusType.COMPLETED, Pageable.unpaged());

        long completedInYear = completedPage.getContent().stream()
                .filter(rs -> rs.getCreatedAt() != null && rs.getCreatedAt().getYear() == year)
                .count();

        // 리뷰 + 평균 별점
        Page<Review> reviewsPage = reviewRepository.findByUserIdAndDeletedFalse(userId, Pageable.unpaged());
        List<Review> reviewsInYear = reviewsPage.getContent().stream()
                .filter(r -> r.getCreatedAt() != null && r.getCreatedAt().getYear() == year)
                .toList();

        long reviewCount = reviewsInYear.size();
        double avgRating = reviewsInYear.stream()
                .map(Review::getStarRating)
                .filter(Objects::nonNull)
                .mapToDouble(Double::doubleValue)
                .average()
                .orElse(0.0);

        return YearlySummaryDto.builder()
                .year(year)
                .completedCount(completedInYear)
                .reviewCount(reviewCount)
                .averageRating(avgRating)
                .build();
    }

    /**
     * 별점 히스토그램 (1~5점)
     */
    public RatingHistogramDto getRatingHistogram(Long userId) {
        Page<Review> page = reviewRepository.findByUserIdAndDeletedFalse(userId, Pageable.unpaged());
        long[] buckets = new long[5]; // index 0 -> 1점, index 4 -> 5점

        page.getContent().forEach(review -> {
            Double rating = review.getStarRating();
            if (rating == null) return;

            int bucket = (int) Math.round(rating); // 0~5 근처
            if (bucket < 1) bucket = 1;
            if (bucket > 5) bucket = 5;

            buckets[bucket - 1]++;
        });

        List<Long> counts = Arrays.stream(buckets)
                .boxed()
                .collect(Collectors.toList());

        return RatingHistogramDto.builder()
                .counts(counts)
                .build();
    }

    /**
     * 가장 많이 읽은(리뷰 작성한) 작가 TOP N
     */
    public TopAuthorsDto getTopAuthors(Long userId, int limit) {
        Page<Review> page = reviewRepository.findByUserIdAndDeletedFalse(userId, Pageable.unpaged());
        List<Review> reviews = page.getContent();

        // 리뷰에서 사용된 bookId 모으기
        Set<Long> bookIds = reviews.stream()
                .map(Review::getBookId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        // bookId -> Book 매핑
        Map<Long, Book> bookMap = bookRepository.findAllById(bookIds).stream()
                .collect(Collectors.toMap(Book::getId, b -> b));

        // author별 카운트
        Map<String, Long> authorCounts = new HashMap<>();
        for (Review review : reviews) {
            Book book = bookMap.get(review.getBookId());
            if (book == null) continue;

            String author = Optional.ofNullable(book.getAuthor()).orElse("알 수 없음");
            authorCounts.merge(author, 1L, Long::sum);
        }

        // 정렬 후 상위 N개만
        List<TopAuthorsDto.AuthorStat> stats = authorCounts.entrySet().stream()
                .sorted((e1, e2) -> Long.compare(e2.getValue(), e1.getValue()))
                .limit(limit)
                .map(e -> TopAuthorsDto.AuthorStat.builder()
                        .author(e.getKey())
                        .reviewCount(e.getValue())
                        .build())
                .toList();

        return TopAuthorsDto.builder()
                .authors(stats)
                .build();
    }

    /**
     * 특정 YearMonth에 대한 목표 달성도
     * - 현재는 Profile에 별도 목표 필드가 없으므로
     *   goal 은 0으로 두고, 나중에 목표 필드가 추가되면 여기만 수정하면 됨.
     */
    public GoalProgressDto getGoalProgress(Long userId, YearMonth ym) {
        int year = ym.getYear();
        int month = ym.getMonthValue();

        // 해당 월에 COMPLETED 된 읽기 상태
        Page<ReadingStatus> page = readingStatusRepository
                .findByUserIdAndStatus(userId, ReadingStatusType.COMPLETED, Pageable.unpaged());

        long completed = page.getContent().stream()
                .filter(rs -> rs.getCreatedAt() != null)
                .filter(rs -> rs.getCreatedAt().getYear() == year &&
                        rs.getCreatedAt().getMonthValue() == month)
                .count();

        // TODO: 실제로는 Profile에 "monthlyGoal" 같은 필드를 두고 읽어와야 함.
        long goal = profileRepository.findByUserId(userId)
                .map(Profile::getReadBook) // 임시로 readBook 사용 (원하면 0L 로 바꿔도 됨)
                .orElse(0L);

        return GoalProgressDto.builder()
                .year(year)
                .month(month)
                .goal(goal)
                .completed(completed)
                .build();
    }
}
