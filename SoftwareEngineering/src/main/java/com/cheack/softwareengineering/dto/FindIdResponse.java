package com.cheack.softwareengineering.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 아이디 찾기 응답용 DTO
 * - username: 찾은 아이디
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FindIdResponse {

    private String username;
}