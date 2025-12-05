package com.cheack.softwareengineering.recommendation;

import com.cheack.softwareengineering.entity.ReadingStatus;
import com.cheack.softwareengineering.entity.ReadingStatusType;
import com.cheack.softwareengineering.entity.Review;
import com.cheack.softwareengineering.entity.ReviewLike;
import com.cheack.softwareengineering.repository.ReadingStatusRepository;
import com.cheack.softwareengineering.repository.ReviewLikeRepository;
import com.cheack.softwareengineering.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 아주 단순한 item-item 기반 추천 엔진 구현.
 *
 * DB 전체를 읽어서(coarse) 공출현 기반으로 근사 추천을 계산한다.
 * - 소규모 프로젝트 / 샘플 데이터 기준으로는 충분히 동작
 * - 나중에 성능이 필요하면 cooccurrence 테이블 + 캐시로 갈아끼우면 됨
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemCooccurrenceEngine implements RecommendationEngine {

    private final ReadingStatusRepository readingStatusRepository;
    private final ReviewRepository reviewRepository;
    private final ReviewLikeRepository reviewLikeRepository;

    /** 한 기준 도서에서 가져올 유사 아이템 개수 (recommendForUser 안에서 사용) */
    private static final int PER_ITEM_NEIGHBORS = 30;

    /** 유저 행동 가중치 */
    private static final double WEIGHT_COMPLETED = 1.0;
    private static final double WEIGHT_READING = 0.7;
    private static final double WEIGHT_WISHLIST = 0.3;
    private static final double WEIGHT_REVIEW_BASE = 0.6;
    private static final double WEIGHT_LIKE = 0.3;

    // ------------------------------------------------------------------------
    // 1) 개인화 추천
    // ------------------------------------------------------------------------

    @Override
    public List<Long> recommendForUser(Long userId, int k) {
        if (userId == null || k <= 0) {
            return Collections.emptyList();
        }

        // 1) 유저 벡터: 이 유저가 어떤 책에 얼마나 강하게 상호작용 했는지
        Map<Long, Double> userVector = buildUserVector(userId);
        if (userVector.isEmpty()) {
            log.debug("[rec] user {} has no interactions", userId);
            return Collections.emptyList();
        }

        Set<Long> seenBookIds = new HashSet<>(userVector.keySet());
        Map<Long, Double> candidateScores = new HashMap<>();

        int neighborK = Math.max(k * 3, PER_ITEM_NEIGHBORS);

        // 2) 유저가 본 각 책에 대해 "비슷한 책"들을 찾고, 가중합으로 점수 부여
        for (Map.Entry<Long, Double> entry : userVector.entrySet()) {
            Long baseBookId = entry.getKey();
            double baseWeight = entry.getValue();

            List<Long> neighbors = similarItems(baseBookId, neighborK);

            for (Long candBookId : neighbors) {
                if (seenBookIds.contains(candBookId)) {
                    // 이미 읽었거나 상호작용한 책은 추천 후보에서 제외
                    continue;
                }
                candidateScores.merge(candBookId, baseWeight, Double::sum);
            }
        }

        return sortAndTrim(candidateScores, k);
    }

    // ------------------------------------------------------------------------
    // 2) 단일 도서 유사 아이템
    // ------------------------------------------------------------------------

    @Override
    public List<Long> similarItems(Long bookId, int k) {
        if (bookId == null || k <= 0) {
            return Collections.emptyList();
        }

        // 모든 상태 / 리뷰 / 좋아요를 한 번씩만 읽어온다.
        List<ReadingStatus> allStatuses = readingStatusRepository.findAll();
        List<Review> allReviews = reviewRepository.findAll();
        List<ReviewLike> allLikes = reviewLikeRepository.findAll();

        // 1) 기준 도서를 본/리뷰한/좋아요 누른 "이웃 유저들" 수집
        Set<Long> neighborUserIds = new HashSet<>();

        for (ReadingStatus rs : allStatuses) {
            if (Objects.equals(rs.getBookId(), bookId)) {
                neighborUserIds.add(rs.getUserId());
            }
        }

        for (Review r : allReviews) {
            if (!r.isDeleted() && Objects.equals(r.getBookId(), bookId)) {
                neighborUserIds.add(r.getUserId());
            }
        }

        // reviewId -> bookId 맵
        Map<Long, Long> reviewBookMap = allReviews.stream()
                .filter(r -> !r.isDeleted())
                .collect(Collectors.toMap(Review::getId, Review::getBookId, (a, b) -> a));

        for (ReviewLike like : allLikes) {
            Long likedBookId = reviewBookMap.get(like.getReviewId());
            if (Objects.equals(likedBookId, bookId)) {
                neighborUserIds.add(like.getUserId());
            }
        }

        if (neighborUserIds.isEmpty()) {
            return Collections.emptyList();
        }

        // 2) 이 이웃 유저들이 본 "다른 책들"을 공출현 카운트로 센다.
        Map<Long, Long> coCounts = new HashMap<>();

        for (ReadingStatus rs : allStatuses) {
            if (!neighborUserIds.contains(rs.getUserId())) continue;
            Long otherBookId = rs.getBookId();
            if (Objects.equals(otherBookId, bookId)) continue;

            coCounts.merge(otherBookId, 1L, Long::sum);
        }

        for (Review r : allReviews) {
            if (!neighborUserIds.contains(r.getUserId())) continue;
            if (r.isDeleted()) continue;
            Long otherBookId = r.getBookId();
            if (Objects.equals(otherBookId, bookId)) continue;

            coCounts.merge(otherBookId, 1L, Long::sum);
        }

        // 필요하다면 좋아요도 공출현에 약하게 반영
        for (ReviewLike like : allLikes) {
            if (!neighborUserIds.contains(like.getUserId())) continue;
            Long otherBookId = reviewBookMap.get(like.getReviewId());
            if (otherBookId == null) continue;
            if (Objects.equals(otherBookId, bookId)) continue;

            coCounts.merge(otherBookId, 1L, Long::sum);
        }

        if (coCounts.isEmpty()) {
            return Collections.emptyList();
        }

        // 3) 공출현 수 기준으로 내림차순 정렬 후 상위 k개 반환
        List<Map.Entry<Long, Long>> sorted = new ArrayList<>(coCounts.entrySet());
        sorted.sort((a, b) -> Long.compare(b.getValue(), a.getValue()));

        return sorted.stream()
                .limit(k)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    // ------------------------------------------------------------------------
    // 내부 헬퍼 메서드들
    // ------------------------------------------------------------------------

    /**
     * 특정 userId 에 대한 "유저 벡터"를 만든다.
     * 각 도서(bookId)에 대해 상호작용 강도(weight)를 합산한 맵을 반환.
     */
    private Map<Long, Double> buildUserVector(Long userId) {
        Map<Long, Double> vector = new HashMap<>();

        List<ReadingStatus> allStatuses = readingStatusRepository.findAll();
        List<Review> allReviews = reviewRepository.findAll();
        List<ReviewLike> allLikes = reviewLikeRepository.findAll();

        // 1) ReadingStatus 기반 가중치
        for (ReadingStatus rs : allStatuses) {
            if (!Objects.equals(rs.getUserId(), userId)) continue;

            double w = switch (rs.getStatus()) {
                case COMPLETED -> WEIGHT_COMPLETED;
                case READING -> WEIGHT_READING;
                case WISHLIST -> WEIGHT_WISHLIST;
                default -> 0.0;
            };

            if (w > 0.0) {
                vector.merge(rs.getBookId(), w, Double::sum);
            }
        }

        // 2) Review 기반 가중치 (별점 반영)
        for (Review r : allReviews) {
            if (!Objects.equals(r.getUserId(), userId)) continue;
            if (r.isDeleted()) continue;

            double w = WEIGHT_REVIEW_BASE;
            if (r.getStarRating() != null) {
                // 0~5 점수를 0~1 로 정규화해서 더해줌
                w += (r.getStarRating() / 5.0);
            }

            vector.merge(r.getBookId(), w, Double::sum);
        }

        // 3) 좋아요 기반 가중치
        Set<Long> likedReviewIds = allLikes.stream()
                .filter(l -> Objects.equals(l.getUserId(), userId))
                .map(ReviewLike::getReviewId)
                .collect(Collectors.toSet());

        if (!likedReviewIds.isEmpty()) {
            List<Review> likedReviews = reviewRepository.findAllById(likedReviewIds);
            for (Review r : likedReviews) {
                if (r.isDeleted()) continue;
                vector.merge(r.getBookId(), WEIGHT_LIKE, Double::sum);
            }
        }

        return vector;
    }

    /**
     * 점수 맵을 점수 내림차순으로 정렬해서 상위 k개의 bookId만 반환.
     */
    private List<Long> sortAndTrim(Map<Long, Double> scores, int k) {
        if (scores.isEmpty() || k <= 0) {
            return Collections.emptyList();
        }

        List<Map.Entry<Long, Double>> sorted = new ArrayList<>(scores.entrySet());
        sorted.sort((a, b) -> Double.compare(b.getValue(), a.getValue()));

        return sorted.stream()
                .limit(k)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }
}