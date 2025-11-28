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

    // 🔽 새로 추가
    private final String authorNickname;
    private final String authorProfileImageUrl;

    private final BookInfo book;

    // 기존 from(...)은 그대로 두고, 내부에서 새 버전 호출하도록 수정
    public static ReviewDetailDto from(Review review, boolean mine) {
        return from(review, mine, null, null, null);
    }

    // 🔽 작성자 정보까지 포함하는 오버로드
    public static ReviewDetailDto from(
            Review review,
            boolean mine,
            String authorNickname,
            String authorProfileImageUrl
    ) {
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
                .authorNickname(authorNickname)
                .authorProfileImageUrl(authorProfileImageUrl)
                .build();
    }

    public static ReviewDetailDto from(
            Review review,
            boolean mine,
            String authorNickname,
            String authorProfileImageUrl,
            BookInfo bookInfo
    ) {
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
                .authorNickname(authorNickname)
                .authorProfileImageUrl(authorProfileImageUrl)
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
