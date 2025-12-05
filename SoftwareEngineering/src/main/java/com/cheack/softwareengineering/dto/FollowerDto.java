package com.cheack.softwareengineering.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 팔로워 리스트용 DTO
 * - username
 * - nickname
 * - profileImageUrl (지금은 null 가능, 나중에 ProfileService 붙이면 채우면 됨)
 * - followedByMe: 현재 로그인한 유저가 이 팔로워를 팔로우 중인지 여부
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FollowerDto {

    private Long id;
    private String username;
    private String nickname;
    private String profileImageUrl;
    private boolean followedByMe;
}