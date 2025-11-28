// src/main/java/com/cheack/softwareengineering/service/FeedService.java
package com.cheack.softwareengineering.service;

import com.cheack.softwareengineering.dto.feed.FeedItemDto;
import com.cheack.softwareengineering.dto.feed.ReviewCardDto;
import com.cheack.softwareengineering.entity.Follow;
import com.cheack.softwareengineering.entity.Review;
import com.cheack.softwareengineering.entity.Visibility;
import com.cheack.softwareengineering.repository.CommentRepository;
import com.cheack.softwareengineering.repository.FollowRepository;
import com.cheack.softwareengineering.repository.ReviewLikeRepository;
import com.cheack.softwareengineering.repository.ReviewRepository;
import com.cheack.softwareengineering.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FeedService {

    private final ReviewRepository reviewRepository;
    private final FollowRepository followRepository;
    private final ReviewLikeRepository reviewLikeRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository; // 지금은 authorId만 쓰지만, 나중에 닉네임/프로필 등 확장 가능

    /**
     * 최신 피드 (전체 사용자 "공개" 리뷰 기준)
     */
    public Page<ReviewCardDto> getLatest(Pageable pageable) {
        // 기존: reviewRepository.findAllByOrderByCreatedAtDesc(pageable);
        Page<Review> reviews =
                reviewRepository.findByVisibilityAndDeletedFalseOrderByCreatedAtDesc(
                        Visibility.PUBLIC, pageable);

        return reviews.map(review -> ReviewCardDto.from(enrich(review, null)));
    }

    /**
     * 팔로잉 피드 (viewerId가 팔로우한 사람들 중 "공개" 리뷰만)
     */
    public Page<ReviewCardDto> getFollowing(Long viewerId, Pageable pageable) {
        List<Follow> follows = followRepository.findByFollowerIdAndStatusTrue(viewerId);

        if (follows.isEmpty()) {
            return new PageImpl<>(Collections.emptyList(), pageable, 0);
        }

        List<Long> followeeIds = follows.stream()
                .map(Follow::getFolloweeId)
                .collect(Collectors.toList());

        // 기존: reviewRepository.findByUserIdInOrderByCreatedAtDesc(followeeIds, pageable);
        Page<Review> reviews =
                reviewRepository.findByUserIdInAndVisibilityAndDeletedFalseOrderByCreatedAtDesc(
                        followeeIds, Visibility.PUBLIC, pageable);

        return reviews.map(review -> ReviewCardDto.from(enrich(review, viewerId)));
    }

    /**
     * Review 하나를 피드 카드에서 쓸 수 있는 DTO로 변환 + 좋아요/댓글 수/내가 좋아요 했는지까지 계산
     */
    public FeedItemDto enrich(Review review, Long viewerId) {
        long likeCount = reviewLikeRepository.countByReviewId(review.getId());
        long commentCount = commentRepository.countByReviewId(review.getId());

        boolean likedByViewer = viewerId != null &&
                reviewLikeRepository.existsByUserIdAndReviewId(viewerId, review.getId());

        String excerpt = buildExcerpt(review.getText());

        return FeedItemDto.builder()
                .reviewId(review.getId())
                .bookId(review.getBookId())
                .authorId(review.getUserId())
                .textExcerpt(excerpt)
                .starRating(review.getStarRating())
                .likeCount(likeCount)
                .commentCount(commentCount)
                .likedByViewer(likedByViewer)
                .createdAt(review.getCreatedAt())
                .build();
    }

    private String buildExcerpt(String text) {
        if (text == null) {
            return null;
        }
        int max = 200;
        if (text.length() <= max) {
            return text;
        }
        return text.substring(0, max) + "...";
    }
}