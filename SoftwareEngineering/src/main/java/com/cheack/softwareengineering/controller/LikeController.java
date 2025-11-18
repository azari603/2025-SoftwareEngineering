package com.cheack.softwareengineering.controller;

import com.cheack.softwareengineering.dto.LikeCountResponse;
import com.cheack.softwareengineering.dto.LikeStatusResponse;
import com.cheack.softwareengineering.dto.ReviewCardDto;
import com.cheack.softwareengineering.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * Comment & Like API v1 중 "좋아요" 부분 담당 컨트롤러
 *
 * Base(좋아요): /api/v1/reviews/{reviewId}/likes
 *
 * - POST   /api/v1/reviews/{reviewId}/likes        (좋아요 생성, 멱등)
 * - DELETE /api/v1/reviews/{reviewId}/likes        (좋아요 취소)
 * - GET    /api/v1/reviews/{reviewId}/likes/count  (좋아요 수)
 * - GET    /api/v1/reviews/{reviewId}/likes/status (내가 좋아요 했는지)
 * - GET    /api/v1/me/likes/reviews                (내가 좋아요한 서평 목록)
 */
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class LikeController {

    private final LikeService likeService;

    /**
     * [리뷰 좋아요 생성]
     * POST /api/v1/reviews/{reviewId}/likes
     * Auth 필요
     */
    @PostMapping("/reviews/{reviewId}/likes")
    public ResponseEntity<Void> like(
            @PathVariable Long reviewId,
            @AuthenticationPrincipal(expression = "id") Long userId
    ) {
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        likeService.like(userId, reviewId);
        // 멱등 create → 201 또는 204 둘 다 가능. 여기선 201 사용.
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * [리뷰 좋아요 취소]
     * DELETE /api/v1/reviews/{reviewId}/likes
     * Auth 필요
     */
    @DeleteMapping("/reviews/{reviewId}/likes")
    public ResponseEntity<Void> unlike(
            @PathVariable Long reviewId,
            @AuthenticationPrincipal(expression = "id") Long userId
    ) {
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        likeService.unlike(userId, reviewId);
        return ResponseEntity.noContent().build();
    }

    /**
     * [리뷰 좋아요 수 조회]
     * GET /api/v1/reviews/{reviewId}/likes/count
     */
    @GetMapping("/reviews/{reviewId}/likes/count")
    public ResponseEntity<LikeCountResponse> getLikeCount(
            @PathVariable Long reviewId
    ) {
        long count = likeService.countByReview(reviewId);
        return ResponseEntity.ok(new LikeCountResponse(count));
    }

    /**
     * [내가 특정 리뷰를 좋아요 했는지]
     * GET /api/v1/reviews/{reviewId}/likes/status
     *
     * 로그인 안 했으면 liked=false 로 응답.
     * 서비스는 이미 완성이라, 상태 조회는
     * getLikedReviews(...) 안에서 찾아보는 방식으로 처리.
     */
    @GetMapping("/reviews/{reviewId}/likes/status")
    public ResponseEntity<LikeStatusResponse> getLikeStatus(
            @PathVariable Long reviewId,
            @AuthenticationPrincipal(expression = "id") Long userId
    ) {
        if (userId == null) {
            return ResponseEntity.ok(new LikeStatusResponse(false));
        }

        // 최대 1000개 정도만 가져와서 안에서 찾는 방식 (서비스는 안 건드림)
        Page<ReviewCardDto> likedPage =
                likeService.getLikedReviews(userId, PageRequest.of(0, 1000));

        boolean liked = likedPage.getContent().stream()
                .anyMatch(card -> reviewId.equals(card.getId()));

        return ResponseEntity.ok(new LikeStatusResponse(liked));
    }

    /**
     * [내가 좋아요한 서평 목록]
     * GET /api/v1/me/likes/reviews?page,size,sort
     * Auth 필요
     */
    @GetMapping("/me/likes/reviews")
    public ResponseEntity<Page<ReviewCardDto>> getMyLikedReviews(
            @AuthenticationPrincipal(expression = "id") Long userId,
            Pageable pageable
    ) {
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Page<ReviewCardDto> page = likeService.getLikedReviews(userId, pageable);
        return ResponseEntity.ok(page);
    }
}