package com.cheack.softwareengineering.service;

import com.cheack.softwareengineering.dto.ReviewCardDto;
import com.cheack.softwareengineering.dto.ReviewCreateRequest;
import com.cheack.softwareengineering.dto.ReviewDetailDto;
import com.cheack.softwareengineering.dto.ReviewUpdateRequest;
import com.cheack.softwareengineering.dto.UserProfileSummaryDto;
import com.cheack.softwareengineering.entity.*;
import com.cheack.softwareengineering.repository.BookRepository;
import com.cheack.softwareengineering.repository.ReadingStatusRepository;
import com.cheack.softwareengineering.repository.ReviewRepository;
import com.cheack.softwareengineering.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    private final ReadingStatusRepository readingStatusRepository;
    private final NotificationService notificationService;
    private final UserService userService;

    /**
     * 서평 생성
     *
     * @return 생성된 reviewId
     */
    @Transactional
    public Long create(Long userId, ReviewCreateRequest req) {
        // book 존재 여부만 간단히 체크
        bookRepository.findById(req.getBookId())
                .orElseThrow(() -> new IllegalArgumentException("Book not found"));

        LocalDateTime now = LocalDateTime.now();

        Review review = Review.builder()
                .userId(userId)
                .bookId(req.getBookId())
                .title(req.getTitle())
                .text(req.getText())
                .starRating(req.getStarRating())
                .visibility(req.getVisibility() != null ? req.getVisibility() : Visibility.PUBLIC)
                .startDate(req.getStartDate())
                .finishDate(req.getFinishDate())
                .createdAt(now)
                .updatedAt(now)
                .deleted(false)
                .build();

        Review saved = reviewRepository.save(review);

        return saved.getId();
    }

    /**
     * 서평 수정 (작성자만)
     */
    @Transactional
    public void update(Long userId, Long reviewId, ReviewUpdateRequest req) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("Review not found"));

        if (!review.getUserId().equals(userId)) {
            throw new IllegalArgumentException("Forbidden");
        }

        if (req.getTitle() != null) {
            review.setTitle(req.getTitle());
        }
        if (req.getText() != null) {
            review.setText(req.getText());
        }
        if (req.getStarRating() != null) {
            review.setStarRating(req.getStarRating());
        }
        if (req.getStartDate() != null) {
            review.setStartDate(req.getStartDate());
        }
        if (req.getFinishDate() != null) {
            review.setFinishDate(req.getFinishDate());
        }
        if (req.getVisibility() != null) {
            review.setVisibility(req.getVisibility());
        }

        review.setUpdatedAt(LocalDateTime.now());
        // JPA dirty checking 으로 자동 flush
    }

    /**
     * 서평 삭제 (소프트 삭제)
     */
    @Transactional
    public void delete(Long userId, Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("Review not found"));

        if (!review.getUserId().equals(userId)) {
            throw new IllegalArgumentException("Forbidden");
        }

        reviewRepository.softDelete(reviewId);
    }

    /**
     * 서평 상세 조회
     */
    public ReviewDetailDto getDetail(Long viewerId, Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("Review not found"));

        if (review.isDeleted()) {
            throw new IllegalArgumentException("Review not found");
        }

        boolean mine = viewerId != null && review.getUserId().equals(viewerId);

        if (!mine && review.getVisibility() == Visibility.PRIVATE) {
            throw new IllegalArgumentException("Forbidden");
        }

        // 작성자 정보 조회 (필드/메서드 이름은 프로젝트에 맞게 바꿔줘)
        UserProfileSummaryDto author = userService.getPublicProfileSummary(review.getUserId());
        String nickname = author != null ? author.getNickname() : null;
        String profileImageUrl = author != null ? author.getProfileImageUrl() : null;

        Book book = bookRepository.findById(review.getBookId())
                .orElse(null);

        Double avgStar = null;
        long reviewCount = 0L;

        if (book != null) {
            // PUBLIC + deleted=false 기준 평균 별점, 서평 개수
            avgStar = reviewRepository.findAvgStarByBookIdAndVisibility(
                    book.getId(),
                    Visibility.PUBLIC
            );
            reviewCount = reviewRepository.countByBookIdAndVisibilityAndDeletedFalse(
                    book.getId(),
                    Visibility.PUBLIC
            );
        }

        ReviewDetailDto.BookInfo bookInfo = null;
        if (book != null) {
            bookInfo = ReviewDetailDto.BookInfo.builder()
                    .name(book.getName())
                    .author(book.getAuthor())
                    .avgStar(avgStar)
                    .reviewCount(reviewCount)
                    .startDate(review.getStartDate())
                    .finishDate(review.getFinishDate())
                    .image(book.getImage())
                    .build();
        }

        return ReviewDetailDto.from(review, mine, nickname, profileImageUrl, bookInfo);
    }

    /**
     * 내 서평 목록
     */
    public Page<ReviewCardDto> getMyReviews(Long userId, Pageable pageable) {
        Page<Review> page =
                reviewRepository.findByUserIdAndDeletedFalse(userId, pageable);

        // 1) 내 프로필 이미지 한 번 조회
        UserProfileSummaryDto me = userService.getPublicProfileSummary(userId);
        String profileImage =
                (me != null ? me.getProfileImageUrl() : null);

        // 2) 이 페이지에 등장하는 bookId 들을 한 번에 로딩해서 map 화
        List<Long> bookIds = page.getContent().stream()
                .map(Review::getBookId)
                .distinct()
                .toList();

        Map<Long, Book> bookMap = bookRepository.findAllById(bookIds).stream()
                .collect(Collectors.toMap(Book::getId, Function.identity()));

        // 3) 각 리뷰에 대해 profileImage + book.image 세팅
        return page.map(review -> {
            Book book = bookMap.get(review.getBookId());
            String bookImage = (book != null ? book.getImage() : null);

            return ReviewCardDto.forMyReviews(
                    review,
                    profileImage,
                    bookImage
            );
        });
    }

    /**
     * 책별 공개 서평 목록
     */
    public Page<ReviewCardDto> getByBook(Long bookId, Pageable pageable) {
        Page<Review> page = reviewRepository
                .findByBookIdAndVisibilityAndDeletedFalse(bookId, Visibility.PUBLIC, pageable);

        // 이 페이지에 등장하는 authorId 들을 한 번에 로딩
        List<Long> authorIds = page.getContent().stream()
                .map(Review::getUserId)
                .distinct()
                .toList();

        Map<Long, User> userMap = userRepository.findAllById(authorIds).stream()
                .collect(Collectors.toMap(User::getId, Function.identity()));

        // 각 리뷰에 대해 author nickname 을 꺼내서 DTO에 채워 넣기
        return page.map(review -> {
            User author = userMap.get(review.getUserId());
            String nickname = (author != null) ? author.getNickname() : null;
            return ReviewCardDto.from(review, nickname);   // ✅ nickname 포함
        });
    }


    /**
     * 특정 사용자의 공개 서평 목록
     */
    public Page<ReviewCardDto> getPublicByUser(Long targetUserId, Pageable pageable) {
        return reviewRepository
                .findByUserIdAndVisibilityAndDeletedFalse(targetUserId, Visibility.PUBLIC, pageable)
                .map(ReviewCardDto::from);
    }
}