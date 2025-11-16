package com.cheack.softwareengineering.service;

import com.cheack.softwareengineering.dto.ReviewCardDto;
import com.cheack.softwareengineering.dto.UserMiniDto;
import com.cheack.softwareengineering.entity.NotificationType;
import com.cheack.softwareengineering.entity.Review;
import com.cheack.softwareengineering.entity.ReviewLike;
import com.cheack.softwareengineering.entity.User;
import com.cheack.softwareengineering.repository.ReviewLikeRepository;
import com.cheack.softwareengineering.repository.ReviewRepository;
import com.cheack.softwareengineering.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LikeService {

    private final ReviewLikeRepository reviewLikeRepository;
    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    /**
     * 좋아요 토글
     * - 아직 좋아요 안 한 상태면 like 수행 후 true 반환
     * - 이미 좋아요 상태면 unlike 수행 후 false 반환
     */
    @Transactional
    public boolean toggle(Long userId, Long reviewId) {
        boolean exists = reviewLikeRepository.existsByUserIdAndReviewId(userId, reviewId);
        if (exists) {
            unlike(userId, reviewId);
            return false;
        } else {
            like(userId, reviewId);
            return true;
        }
    }

    /**
     * 좋아요 생성 (멱등 보장)
     */
    @Transactional
    public void like(Long userId, Long reviewId) {
        // 이미 좋아요라면 아무 것도 안 함
        if (reviewLikeRepository.existsByUserIdAndReviewId(userId, reviewId)) {
            return;
        }

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("Review not found"));

        ReviewLike like = ReviewLike.builder()
                .userId(userId)
                .reviewId(reviewId)
                .build();

        reviewLikeRepository.save(like);

        // 서평 작성자에게 좋아요 알림
        Long receiverId = review.getUserId();

        // 자기 서평 좋아요 알림을 막고 싶으면 이 조건 유지
        if (!Objects.equals(receiverId, userId)) {
            String targetUrl = "/reviews/" + reviewId;
            String content = "회원님의 서평에 좋아요가 추가되었습니다.";

            notificationService.create(
                    receiverId,              // 알림 받는 사람
                    userId,                  // 행동한 사람
                    NotificationType.REVIEW_LIKE,
                    targetUrl,
                    content,
                    reviewId                 // 어떤 서평에 대한 알림인지
            );
        }
    }

    /**
     * 좋아요 취소 (멱등)
     */
    @Transactional
    public void unlike(Long userId, Long reviewId) {
        reviewLikeRepository.deleteByUserIdAndReviewId(userId, reviewId);
    }

    /**
     * 특정 서평을 좋아요 한 유저 목록
     */
    public Page<UserMiniDto> getLikers(Long reviewId, Pageable pageable) {
        Page<ReviewLike> page = reviewLikeRepository.findByReviewId(reviewId, pageable);

        List<Long> userIds = page.getContent().stream()
                .map(ReviewLike::getUserId)
                .toList();

        var userMap = userRepository.findAllById(userIds).stream()
                .collect(Collectors.toMap(User::getId, Function.identity()));

        List<UserMiniDto> dtos = page.getContent().stream()
                .map(like -> userMap.get(like.getUserId()))
                .filter(Objects::nonNull)
                .map(this::toUserMiniDto)
                .toList();

        return new PageImpl<>(dtos, pageable, page.getTotalElements());
    }

    /**
     * 내가 좋아요 한 서평 목록
     */
    public Page<ReviewCardDto> getLikedReviews(Long userId, Pageable pageable) {
        Page<ReviewLike> page = reviewLikeRepository.findByUserId(userId, pageable);

        List<Long> reviewIds = page.getContent().stream()
                .map(ReviewLike::getReviewId)
                .toList();

        var reviewMap = reviewRepository.findAllById(reviewIds).stream()
                .collect(Collectors.toMap(Review::getId, Function.identity()));

        List<ReviewCardDto> dtos = page.getContent().stream()
                .map(like -> reviewMap.get(like.getReviewId()))
                .filter(Objects::nonNull)
                .map(this::toReviewCardDto)
                .toList();

        return new PageImpl<>(dtos, pageable, page.getTotalElements());
    }

    /**
     * 서평별 좋아요 개수
     */
    public long countByReview(Long reviewId) {
        return reviewLikeRepository.countByReviewId(reviewId);
    }

    // ==== 내부 변환 메서드들 ====

    private UserMiniDto toUserMiniDto(User user) {
        return UserMiniDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .nickname(user.getNickname())
                .build();
    }

    private ReviewCardDto toReviewCardDto(Review review) {
        return ReviewCardDto.builder()
                .id(review.getId())                // ← 여기 reviewId → id 로 수정
                .bookId(review.getBookId())
                .title(review.getTitle())
                .excerpt(extractExcerpt(review.getText()))
                .starRating(review.getStarRating())
                .createdAt(review.getCreatedAt())
                .build();
    }

    private String extractExcerpt(String text) {
        if (text == null) return "";
        return text.length() <= 100 ? text : text.substring(0, 100) + "...";
    }
}