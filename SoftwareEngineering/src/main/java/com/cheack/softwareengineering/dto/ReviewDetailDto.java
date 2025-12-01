// src/main/java/com/cheack/softwareengineering/dto/ReviewDetailDto.java
package com.cheack.softwareengineering.dto;

import com.cheack.softwareengineering.entity.Review;
import com.cheack.softwareengineering.entity.Visibility;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@Builder
public class ReviewDetailDto {

    private final Long id;
    private final Long userId;
    private final Long bookId;

    private final String title;
    private final String text;
    private final Double starRating;
    private final Visibility visibility;
    private final LocalDate startDate;
    private final LocalDate finishDate;

    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    private final boolean mine; // viewer 기준 본인 글인지 여부

    // 🔹 작성자 정보
    private final String authorUsername;         // 새로 추가
    private final String authorNickname;
    private final String authorProfileImageUrl;

    // 🔹 댓글 개수
    private final long commentCount;             // 새로 추가

    private final BookInfo book;

    // === 기존 from(...)은 호환성 유지용으로 남겨둠 ===

    public static ReviewDetailDto from(Review review, boolean mine) {
        // 예전 코드에서 쓰던 곳이 있을 수 있으니, 안전하게 기본값으로 채워 줌
        return from(review, mine, null, null, null, null, 0L);
    }

    /**
     * 작성자/책/댓글정보까지 한 번에 채우는 팩토리 메서드
     */
    public static ReviewDetailDto from(
            Review review,
            boolean mine,
            String authorUsername,
            String authorNickname,
            String authorProfileImageUrl,
            BookInfo bookInfo,
            Long commentCount
    ) {
        long safeCommentCount = (commentCount != null ? commentCount : 0L);

        return ReviewDetailDto.builder()
                .id(review.getId())
                .userId(review.getUserId())
                .bookId(review.getBookId())
                .title(review.getTitle())
                .text(review.getText())
                .starRating(review.getStarRating())
                .visibility(review.getVisibility())
                .startDate(review.getStartDate())
                .finishDate(review.getFinishDate())
                .createdAt(review.getCreatedAt())
                .updatedAt(review.getUpdatedAt())
                .mine(mine)
                .authorUsername(authorUsername)
                .authorNickname(authorNickname)
                .authorProfileImageUrl(authorProfileImageUrl)
                .commentCount(safeCommentCount)
                .book(bookInfo)
                .build();
    }

    @Getter
    @AllArgsConstructor
    @Builder
    public static class BookInfo {
        private final String name;
        private final String author;
        private final Double avgStar;
        private final long reviewCount;
        private final LocalDate startDate;
        private final LocalDate finishDate;
        private final String image;
    }
}