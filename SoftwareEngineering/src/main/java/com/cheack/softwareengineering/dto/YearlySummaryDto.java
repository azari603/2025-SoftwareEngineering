package com.cheack.softwareengineering.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 한 해 동안의 요약 통계
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class YearlySummaryDto {

    private int year;

    private long completedCount;
    private long reviewCount;
    private double averageRating;
}
