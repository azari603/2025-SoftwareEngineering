// src/main/java/com/cheack/softwareengineering/dto/feed/FeedItemDto.java
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
public class FeedItemDto {

    private Long reviewId;
    private Long bookId;
    private Long authorId;

    // 본문 일부만 잘라서 보여줄 때 사용
    private String textExcerpt;

    private Double starRating;

    private long likeCount;
    private long commentCount;
    private boolean likedByViewer;

    private LocalDateTime createdAt;
}