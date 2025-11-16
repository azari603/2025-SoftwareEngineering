package com.cheack.softwareengineering.service;

import com.cheack.softwareengineering.dto.ReviewCardDto;
import com.cheack.softwareengineering.dto.ReviewCreateRequest;
import com.cheack.softwareengineering.dto.ReviewDetailDto;
import com.cheack.softwareengineering.dto.ReviewUpdateRequest;
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

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    private final ReadingStatusRepository readingStatusRepository;
    private final NotificationService notificationService;

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

        boolean mine = review.getUserId().equals(viewerId);

        if (!mine && review.getVisibility() == Visibility.PRIVATE) {
            throw new IllegalArgumentException("Forbidden");
        }

        return ReviewDetailDto.from(review, mine);
    }

    /**
     * 내 서평 목록
     */
    public Page<ReviewCardDto> getMyReviews(Long userId, Pageable pageable) {
        return reviewRepository.findByUserIdAndDeletedFalse(userId, pageable)
                .map(ReviewCardDto::from);
    }

    /**
     * 책별 공개 서평 목록
     */
    public Page<ReviewCardDto> getByBook(Long bookId, Pageable pageable) {
        return reviewRepository
                .findByBookIdAndVisibilityAndDeletedFalse(bookId, Visibility.PUBLIC, pageable)
                .map(ReviewCardDto::from);
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