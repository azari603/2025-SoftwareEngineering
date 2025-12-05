package com.cheack.softwareengineering.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 특정 연도 기준 월별 통계(예: 완독 권수)를 담는 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MonthlySeriesDto {

    private int year;

    /**
     * 1월~12월 순서로 들어가는 리스트.
     * index 0 -> 1월, index 11 -> 12월
     */
    private List<Long> completedByMonth;
}
