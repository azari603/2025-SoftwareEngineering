package com.cheack.softwareengineering.dto;

import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CheckResponse {
    private boolean available;
    private String message;
}