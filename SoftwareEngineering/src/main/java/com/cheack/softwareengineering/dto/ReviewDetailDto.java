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

    public static ReviewDetailDto from(Review review, boolean mine) {
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
                .build();
    }
}