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

    @Transactional
    public void like(Long userId, Long reviewId) {
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

        Long receiverId = review.getUserId();
        if (!Objects.equals(receiverId, userId)) {
            notificationService.create(
                    receiverId,
                    userId,
                    NotificationType.REVIEW_LIKE,
                    "/reviews/" + reviewId,
                    null,
                    reviewId
            );
        }
    }

    @Transactional
    public void unlike(Long userId, Long reviewId) {
        reviewLikeRepository.deleteByUserIdAndReviewId(userId, reviewId);
    }

    public Page<UserMiniDto> getLikers(Long reviewId, Pageable pageable) {
        Page<ReviewLike> page = reviewLikeRepository.findByReviewId(reviewId, pageable);
        List<Long> userIds = page.getContent().stream().map(ReviewLike::getUserId).toList();
        var userMap = userRepository.findAllById(userIds).stream()
                .collect(Collectors.toMap(User::getId, Function.identity()));
        List<UserMiniDto> dtos = page.getContent().stream()
                .map(like -> userMap.get(like.getUserId()))
                .filter(java.util.Objects::nonNull)
                .map(this::toUserMiniDto)
                .toList();
        return new PageImpl<>(dtos, pageable, page.getTotalElements());
    }

    public Page<ReviewCardDto> getLikedReviews(Long userId, Pageable pageable) {
        // 내가 좋아요 누른 기록
        Page<ReviewLike> page = reviewLikeRepository.findByUserId(userId, pageable);

        // 좋아요 대상 리뷰 IDs
        List<Long> reviewIds = page.getContent().stream()
                .map(ReviewLike::getReviewId)
                .toList();

        // 리뷰 엔티티 조회 후 맵핑
        var reviewMap = reviewRepository.findAllById(reviewIds).stream()
                .collect(Collectors.toMap(Review::getId, Function.identity()));

        // 리뷰 작성자 IDs
        var authorIds = reviewMap.values().stream()
                .map(Review::getUserId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        // 작성자 User 엔티티 조회 후 맵핑
        var userMap = userRepository.findAllById(authorIds).stream()
                .collect(Collectors.toMap(User::getId, Function.identity()));

        // 좋아요 목록 순서를 유지하면서 DTO로 변환
        List<ReviewCardDto> dtos = page.getContent().stream()
                .map(like -> {
                    Review review = reviewMap.get(like.getReviewId());
                    if (review == null) {
                        return null;
                    }
                    User author = userMap.get(review.getUserId());
                    return toReviewCardDto(review, author);
                })
                .filter(Objects::nonNull)
                .toList();

        return new PageImpl<>(dtos, pageable, page.getTotalElements());
    }

    public long countByReview(Long reviewId) {
        return reviewLikeRepository.countByReviewId(reviewId);
    }

    private UserMiniDto toUserMiniDto(User user) {
        return UserMiniDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .nickname(user.getNickname())
                .build();
    }

    private ReviewCardDto toReviewCardDto(Review review, User author) {
        String excerpt = extractExcerpt(review.getText());

        String username = author != null ? author.getUsername() : null;
        String nickname = author != null ? author.getNickname() : null;

        return ReviewCardDto.builder()
                .id(review.getId())
                .bookId(review.getBookId())
                .userId(review.getUserId())
                .title(review.getTitle())
                .excerpt(excerpt)
                .starRating(review.getStarRating())
                .visibility(review.getVisibility())
                .createdAt(review.getCreatedAt())

                // 작성자 정보 채우기
                .username(username)
                .nickname(nickname)
                .profileImage(null)   // 필요하다면 User에 있는 필드로 채워도 됨

                // 좋아요/댓글 카운트는 이 API 요구사항에 없으니 0
                .book(null)
                .likeCount(0L)
                .commentCount(0L)
                .likedByViewer(true)  // "내가 좋아요한 리뷰 목록"이라 항상 true 로 둘 수도 있음
                .build();
    }

    private String extractExcerpt(String text) {
        if (text == null) return "";
        return text.length() <= 100 ? text : text.substring(0, 100) + "...";
    }
}
