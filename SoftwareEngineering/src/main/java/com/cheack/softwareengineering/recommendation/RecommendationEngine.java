package com.cheack.softwareengineering.recommendation;

import java.util.List;

/**
 * 추천 알고리즘 엔진 공통 인터페이스.
 *
 * 구현체 예시:
 * - ItemCooccurrenceEngine (item-item 기반)
 * - UserEmbeddingEngine 등
 */
public interface RecommendationEngine {

    /**
     * 주어진 사용자에게 추천할 도서 ID 목록을 점수순으로 반환한다.
     *
     * @param userId 추천 대상 사용자 ID
     * @param k      최대 추천 개수
     * @return bookId 리스트 (중복 없음, 정렬된 상태)
     */
    List<Long> recommendForUser(Long userId, int k);

    /**
     * 기준 도서와 비슷한 도서 ID 목록을 반환한다.
     *
     * @param bookId 기준 도서 ID
     * @param k      최대 개수
     */
    List<Long> similarItems(Long bookId, int k);

    /**
     * 리뷰/좋아요/상태 변경 등 이벤트가 생겼을 때 호출해서
     * 내부 캐시/모델을 미세 업데이트할 수 있게 해주는 훅.
     * 지금은 쓰지 않으므로 빈 구현으로 둬도 된다.
     */
    default void update(RecommendationEvent event) {
        // no-op (필요하면 구현체에서 override)
    }

    /**
     * 배치로 전체 그래프/모델을 다시 계산하고 싶을 때 사용.
     * 지금은 사용 안 해도 됨.
     */
    default void rebuild() {
        // no-op
    }
}