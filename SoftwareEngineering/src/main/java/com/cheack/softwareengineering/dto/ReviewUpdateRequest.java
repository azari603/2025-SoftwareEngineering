package com.cheack.softwareengineering.dto;

import com.cheack.softwareengineering.entity.Visibility;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewUpdateRequest {

    private String title;
    private String text;
    private Double starRating;
    private LocalDate startDate;
    private LocalDate finishDate;
    private Visibility visibility;
}