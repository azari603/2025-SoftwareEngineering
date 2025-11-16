package com.cheack.softwareengineering.dto;

import com.cheack.softwareengineering.entity.Review;
import com.cheack.softwareengineering.entity.Visibility;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@Builder
public class ReviewCardDto {

    private final Long id;
    private final Long bookId;
    private final Long userId;

    private final String title;
    private final String excerpt;
    private final Double starRating;
    private final Visibility visibility;

    private final LocalDateTime createdAt;

    public static ReviewCardDto from(Review review) {
        String text = review.getText();
        String excerpt = text == null
                ? ""
                : (text.length() <= 120 ? text : text.substring(0, 117) + "...");

        return ReviewCardDto.builder()
                .id(review.getId())
                .bookId(review.getBookId())
                .userId(review.getUserId())
                .title(review.getTitle())
                .excerpt(excerpt)
                .starRating(review.getStarRating())
                .visibility(review.getVisibility())
                .createdAt(review.getCreatedAt())
                .build();
    }
}