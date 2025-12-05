package com.cheack.softwareengineering.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 독서 목표 달성도 DTO
 * - 지금은 Profile에 별도 목표 필드가 없어서, goal 은 임시로 넣어두고
 *   실제 목표 필드가 생기면 StatsService 쪽 로직만 수정하면 됨.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GoalProgressDto {

    private int year;
    private int month;

    // 목표 권수 (임시: 0 또는 프로필에서 가져온 값)
    private long goal;

    // 해당 월에 실제 완독한 권수
    private long completed;
}
