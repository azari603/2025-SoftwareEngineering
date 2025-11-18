package com.cheack.softwareengineering.controller;

import com.cheack.softwareengineering.dto.ReviewCardDto;
import com.cheack.softwareengineering.dto.ReviewCreateRequest;
import com.cheack.softwareengineering.dto.ReviewDetailDto;
import com.cheack.softwareengineering.dto.ReviewUpdateRequest;
import com.cheack.softwareengineering.dto.UserDto;
import com.cheack.softwareengineering.service.ReviewService;
import com.cheack.softwareengineering.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * Review API v1
 *
 * Base: /api/v1/reviews
 *
 * - POST   /api/v1/reviews                   : 서평 작성
 * - GET    /api/v1/reviews/{reviewId}        : 서평 상세 조회
 * - GET    /api/v1/reviews/me                : 내 서평 목록
 * - GET    /api/v1/reviews/books/{bookId}    : 책별 공개 서평 목록
 * - GET    /api/v1/reviews/users/{username}  : 특정 유저의 공개 서평 목록
 * - PATCH  /api/v1/reviews/{reviewId}        : 서평 수정(본인만)
 * - DELETE /api/v1/reviews/{reviewId}        : 서평 삭제(본인만, 소프트 삭제)
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/reviews")
public class ReviewController {

    private final ReviewService reviewService;
    private final UserService userService; // username -> userId 매핑용

    /**
     * 서평 작성
     * POST /api/v1/reviews
     */
    @PostMapping
    public ResponseEntity<Long> createReview(
            @AuthenticationPrincipal(expression = "id") Long userId,
            @RequestBody ReviewCreateRequest request
    ) {
        if (userId == null) {
            // TODO: 공통 예외로 교체 (UNAUTHORIZED)
            throw new IllegalArgumentException("UNAUTHORIZED");
        }

        Long reviewId = reviewService.create(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(reviewId);
    }

    /**
     * 서평 상세 조회
     * GET /api/v1/reviews/{reviewId}
     *
     * 비로그인도 접근 가능해야 하므로, principal 이 없으면 viewerId 에 0L 같은
     * 더미값을 넣어서 ReviewService.getDetail(...) 에 넘긴다.
     */
    @GetMapping("/{reviewId}")
    public ReviewDetailDto getReviewDetail(
            @PathVariable Long reviewId,
            @AuthenticationPrincipal(expression = "id") Long userId
    ) {
        Long viewerId = (userId != null) ? userId : 0L;
        return reviewService.getDetail(viewerId, reviewId);
    }

    /**
     * 내 서평 목록
     * GET /api/v1/reviews/me
     */
    @GetMapping("/me")
    public Page<ReviewCardDto> getMyReviews(
            @AuthenticationPrincipal(expression = "id") Long userId,
            Pageable pageable
    ) {
        if (userId == null) {
            throw new IllegalArgumentException("UNAUTHORIZED");
        }
        return reviewService.getMyReviews(userId, pageable);
    }

    /**
     * 책별 공개 서평 목록
     * GET /api/v1/reviews/books/{bookId}
     */
    @GetMapping("/books/{bookId}")
    public Page<ReviewCardDto> getReviewsByBook(
            @PathVariable Long bookId,
            Pageable pageable
    ) {
        return reviewService.getByBook(bookId, pageable);
    }

    /**
     * 특정 유저의 공개 서평 목록 (username 기준)
     * GET /api/v1/reviews/users/{username}
     */
    @GetMapping("/users/{username}")
    public Page<ReviewCardDto> getPublicReviewsByUser(
            @PathVariable String username,
            Pageable pageable
    ) {
        // UserService 를 통해 username -> userId 해결
        UserDto user = userService.getByUsername(username);
        Long targetUserId = user.getId();

        return reviewService.getPublicByUser(targetUserId, pageable);
    }

    /**
     * 서평 수정 (본인만)
     * PATCH /api/v1/reviews/{reviewId}
     */
    @PatchMapping("/{reviewId}")
    public ResponseEntity<Void> updateReview(
            @PathVariable Long reviewId,
            @AuthenticationPrincipal(expression = "id") Long userId,
            @RequestBody ReviewUpdateRequest request
    ) {
        if (userId == null) {
            throw new IllegalArgumentException("UNAUTHORIZED");
        }

        reviewService.update(userId, reviewId, request);
        return ResponseEntity.noContent().build();
    }

    /**
     * 서평 삭제(소프트 삭제, 본인만)
     * DELETE /api/v1/reviews/{reviewId}
     */
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<Void> deleteReview(
            @PathVariable Long reviewId,
            @AuthenticationPrincipal(expression = "id") Long userId
    ) {
        if (userId == null) {
            throw new IllegalArgumentException("UNAUTHORIZED");
        }

        reviewService.delete(userId, reviewId);
        return ResponseEntity.noContent().build();
    }
}