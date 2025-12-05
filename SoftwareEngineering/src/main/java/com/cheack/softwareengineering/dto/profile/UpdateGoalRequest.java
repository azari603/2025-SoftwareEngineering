package com.cheack.softwareengineering.dto.profile;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * PATCH /profiles/me/goal 요청 바디
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateGoalRequest {

    private int monthlyGoal;
}
