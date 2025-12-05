package com.cheack.softwareengineering.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class FollowCountResponse {

    private long followerCount;
    private long followingCount;
}