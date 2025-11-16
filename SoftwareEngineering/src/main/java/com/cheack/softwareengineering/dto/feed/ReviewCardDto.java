// src/main/java/com/cheack/softwareengineering/dto/feed/ReviewCardDto.java
package com.cheack.softwareengineering.dto.feed;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewCardDto {

    private Long reviewId;
    private Long bookId;
    private Long authorId;

    private String textExcerpt;
    private Double starRating;

    private long likeCount;
    private long commentCount;
    private boolean likedByViewer;

    private LocalDateTime createdAt;

    public static ReviewCardDto from(FeedItemDto item) {
        return ReviewCardDto.builder()
                .reviewId(item.getReviewId())
                .bookId(item.getBookId())
                .authorId(item.getAuthorId())
                .textExcerpt(item.getTextExcerpt())
                .starRating(item.getStarRating())
                .likeCount(item.getLikeCount())
                .commentCount(item.getCommentCount())
                .likedByViewer(item.isLikedByViewer())
                .createdAt(item.getCreatedAt())
                .build();
    }
}