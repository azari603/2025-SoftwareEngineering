package com.cheack.softwareengineering.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 별점 히스토그램(1~5점 카운트)
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RatingHistogramDto {

    /**
     * index 0 -> 1점, index 4 -> 5점
     */
    private List<Long> counts;
}
