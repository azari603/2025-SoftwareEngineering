package com.cheack.softwareengineering.dto;

import com.cheack.softwareengineering.entity.ReadingStatus;
import com.cheack.softwareengineering.entity.ReadingStatusType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookReadingStatusResponse {

    private Long bookId;
    private boolean hasStatus;
    private ReadingStatusType status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static BookReadingStatusResponse from(ReadingStatus entity) {
        return BookReadingStatusResponse.builder()
                .bookId(entity.getBookId())
                .hasStatus(true)
                .status(entity.getStatus())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public static BookReadingStatusResponse empty(Long bookId) {
        return BookReadingStatusResponse.builder()
                .bookId(bookId)
                .hasStatus(false)
                .status(null)
                .createdAt(null)
                .updatedAt(null)
                .build();
    }
}