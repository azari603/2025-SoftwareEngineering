package com.cheack.softwareengineering.recommendation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 추천 엔진에 전달할 수 있는 아주 단순한 이벤트 표현.
 * 지금은 사용하지 않지만, 시그니처 맞추려고 만들어 둔 상태.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecommendationEvent {

    public enum EventType {
        REVIEW_CREATED,
        REVIEW_UPDATED,
        REVIEW_DELETED,
        LIKE_ADDED,
        LIKE_REMOVED,
        READING_STATUS_CHANGED
    }

    private EventType type;
    private Long userId;
    private Long bookId;
}