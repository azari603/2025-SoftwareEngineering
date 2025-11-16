package com.cheack.softwareengineering.dto;

import com.cheack.softwareengineering.entity.ReadingStatusType;
import lombok.Builder;
import lombok.Getter;

import java.util.EnumMap;
import java.util.Map;

@Getter
@Builder
public class ReadingStatusSummary {

    private long totalCount;
    private Map<ReadingStatusType, Long> countsByStatus;

    public static ReadingStatusSummary of(Map<ReadingStatusType, Long> counts) {
        long total = counts.values().stream().mapToLong(Long::longValue).sum();
        return ReadingStatusSummary.builder()
                .totalCount(total)
                .countsByStatus(new EnumMap<>(counts))
                .build();
    }
}
