package com.cheack.softwareengineering.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RatingHistogramDto {

    /**
     * index 0 -> 1점, index 4 -> 5점
     */
    private List<Long> counts;

    /**
     * 각 별점(1~5점)에 해당하는 책 리스트
     * - 예: rating = 5 인 bucket 에 5점 준 책들 목록
     * - 필요 없다면 null 로 내려가도 된다.
     */
    private List<RatingBucket> buckets;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class RatingBucket {
        private int rating;                // 1 ~ 5
        private List<BookCardDto> books;   // 해당 별점의 책 목록
    }
}
