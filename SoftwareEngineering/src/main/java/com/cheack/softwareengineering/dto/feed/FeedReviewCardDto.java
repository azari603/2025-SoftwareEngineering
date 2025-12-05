package com.cheack.softwareengineering.dto.feed;

import com.cheack.softwareengineering.entity.Review;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeedReviewCardDto {

    private Long reviewId;
    private Long bookId;
    private Long authorId;

    // 서평 정보
    private String title;         // 서평 제목
    private String textExcerpt;   // 서평 내용 요약
    private Double starRating;    // 이 서평의 별점

    // 좋아요/댓글 정보
    private long likeCount;
    private long commentCount;
    private boolean likedByViewer;

    private LocalDateTime createdAt;

    // 작성자 정보
    private String nickname;
    private String username;
    private String profileImage;

    // 도서 정보
    private String bookName;
    private String bookImage;
    private String bookAuthor;
    private Double avgStar;       // 이 책의 평균 별점(공개 서평 기준 등)

    public static FeedReviewCardDto from(
            Review review,
            FeedItemDto item,
            String nickname,
            String username,
            String profileImage,
            String bookName,
            String bookImage,
            String bookAuthor,
            Double avgStar
    ) {
        return FeedReviewCardDto.builder()
                .reviewId(review.getId())
                .bookId(review.getBookId())
                .authorId(review.getUserId())
                .title(review.getTitle())
                .textExcerpt(item != null ? item.getTextExcerpt() : null)
                .starRating(review.getStarRating())
                .likeCount(item != null ? item.getLikeCount() : 0L)
                .commentCount(item != null ? item.getCommentCount() : 0L)
                .likedByViewer(item != null && item.isLikedByViewer())
                .createdAt(review.getCreatedAt())
                .nickname(nickname)
                .username(username)
                .profileImage(profileImage)
                .bookName(bookName)
                .bookImage(bookImage)
                .bookAuthor(bookAuthor)
                .avgStar(avgStar)
                .build();
    }
}
