package com.cheack.softwareengineering.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UnreadCountResponse {

    private final long unreadCount;
}