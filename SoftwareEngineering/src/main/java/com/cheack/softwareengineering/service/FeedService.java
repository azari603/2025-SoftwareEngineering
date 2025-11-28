// src/main/java/com/cheack/softwareengineering/service/FeedService.java
package com.cheack.softwareengineering.service;

import com.cheack.softwareengineering.dto.feed.FeedItemDto;
import com.cheack.softwareengineering.dto.feed.FeedReviewCardDto;
import com.cheack.softwareengineering.entity.*;
import com.cheack.softwareengineering.repository.*;
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
    private final ProfileRepository profileRepository;
    private final BookRepository bookRepository;

    /**
     * 최신 피드 (전체 사용자 "공개" 리뷰 기준)
     */
    public Page<FeedReviewCardDto> getLatest(Pageable pageable) {
        // 기존: reviewRepository.findAllByOrderByCreatedAtDesc(pageable);
        Page<Review> reviews =
                reviewRepository.findByVisibilityAndDeletedFalseOrderByCreatedAtDesc(
                        Visibility.PUBLIC, pageable);

        return reviews.map(review -> buildFeedReviewCard(review, null));
    }

    /**
     * 팔로잉 피드 (viewerId가 팔로우한 사람들 중 "공개" 리뷰만)
     */
    public Page<FeedReviewCardDto> getFollowing(Long viewerId, Pageable pageable) {
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

        return reviews.map(review -> buildFeedReviewCard(review, null));    }



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

    /**
     * 단일 피드 아이템
     */
    public FeedReviewCardDto getFeedItem(Long reviewId, Long viewerId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("Review not found"));
        return buildFeedReviewCard(review, viewerId);
    }

    private FeedReviewCardDto buildFeedReviewCard(Review review, Long viewerId) {
        // 좋아요/댓글/내가 좋아요 했는지
        FeedItemDto item = enrich(review, viewerId);

        // 작성자 정보
        Long authorId = review.getUserId();
        User author = userRepository.findById(authorId).orElse(null);
        Profile profile = profileRepository.findByUserId(authorId).orElse(null);

        String nickname = author != null ? author.getNickname() : null;
        String username = author != null ? author.getUsername() : null;
        String profileImage = profile != null ? profile.getUserImage() : null;

        // 도서 정보
        Long bookId = review.getBookId();
        Book book = bookRepository.findById(bookId).orElse(null);

        String bookName = book != null ? book.getName() : null;
        String bookImage = book != null ? book.getImage() : null;
        String bookAuthor = book != null ? book.getAuthor() : null;

        // 이 책의 평균 별점(공개 서평 기준)
        Double avgStar = reviewRepository.findAvgStarByBookIdAndVisibility(
                bookId,
                Visibility.PUBLIC
        );
        return FeedReviewCardDto.from(
                review,
                item,
                nickname,
                username,
                profileImage,
                bookName,
                bookImage,
                bookAuthor,
                avgStar
        );
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