package com.cheack.softwareengineering.controller;

import com.cheack.softwareengineering.dto.UserDto;
import com.cheack.softwareengineering.dto.BookCardDto;
import com.cheack.softwareengineering.service.UserService;
import com.cheack.softwareengineering.service.RecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * Recommendation API v1
 *
 * Base path: /api/v1
 *
 * - GET /api/v1/recommendations/me        : 개인화 추천 (로그인 필요)
 * - GET /api/v1/recommendations/popular   : 인기 도서 폴백
 * - GET /api/v1/books/{bookId}/similar    : 유사 도서 추천
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class RecommendationController {

    private final UserService userService;
    private final RecommendationService recommendationService;

    /**
     * 개인화 추천
     * GET /api/v1/recommendations/me
     *
     * Auth: Bearer 토큰 필수
     */
    @GetMapping("/recommendations/me")
    public Page<BookCardDto> getMyRecommendations(
            @AuthenticationPrincipal String username,
            Pageable pageable
    ) {
        if (username == null || "anonymousUser".equals(username)) {
            throw new IllegalArgumentException("UNAUTHORIZED");
        }

        //if (userId == null) {
            // TODO: 나중에 커스텀 예외/에러코드(UNAUTHORIZED)로 교체
        //    throw new IllegalArgumentException("UNAUTHORIZED");
        //}

        UserDto user = userService.getByUsername(username);
        Long userId = user.getId();

        return recommendationService.recommendForUser(userId, pageable);
    }

    /**
     * 인기 도서(폴백)
     * GET /api/v1/recommendations/popular
     */
    @GetMapping("/recommendations/popular")
    public Page<BookCardDto> getPopularRecommendations(Pageable pageable) {
        return recommendationService.fallbackPopular(pageable);
    }

    /**
     * 유사 도서
     * GET /api/v1/books/{bookId}/similar
     *
     * (경로는 /books/... 이지만, 구현은 여기서 담당한다)
     */
    @GetMapping("/books/{bookId}/similar")
    public Page<BookCardDto> getSimilarBooks(
            @PathVariable Long bookId,
            Pageable pageable
    ) {
        return recommendationService.similarBooks(bookId, pageable);
    }
}