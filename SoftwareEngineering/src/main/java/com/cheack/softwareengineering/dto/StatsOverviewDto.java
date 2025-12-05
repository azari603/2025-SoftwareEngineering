package com.cheack.softwareengineering.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StatsOverviewDto {

    // 읽기 상태별 카운트
    private long completedCount;
    private long readingCount;
    private long wishlistCount;

    // 작성한 리뷰 수
    private long reviewCount;

    // 평균 별점 (리뷰 기준)
    private double averageRating;
}
