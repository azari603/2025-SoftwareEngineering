package com.cheack.softwareengineering.controller;

import com.cheack.softwareengineering.dto.*;
import com.cheack.softwareengineering.service.StatsService;
import com.cheack.softwareengineering.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.YearMonth;
import java.util.List;

/**
 * Stats & Goals API v1
 *
 * Base: /api/v1/stats
 *
 * 설계서에 나온 엔드포인트:
 *  - GET /stats/me/overview?period=month|year&from=YYYY-MM(또는 YYYY)&to=YYYY-MM
 *  - GET /stats/me/stars
 *  - GET /stats/me/timeline?granularity=month|year&from=YYYY-MM&to=YYYY-MM
 *  - GET /stats/me/authors?top=10
 *  - GET /stats/me/categories
 *  - GET /stats/me/goals?period=month|year
 *  - GET /stats/users/{username}/overview?period=month|year
 */
@RestController
@RequestMapping("/api/v1/stats")
@RequiredArgsConstructor
public class StatsController {

    private final StatsService statsService;
    private final UserService userService;

    // ======================= 공통/개요 =======================

    /**
     * [내 통계 개요]
     * GET /api/v1/stats/me/overview?period=month|year&from=...&to=...
     */
    @GetMapping("/me/overview")
    public StatsOverviewDto getMyOverview(
            @AuthenticationPrincipal String username,
            @RequestParam(name = "period", required = false) String period,
            @RequestParam(name = "from",   required = false) String from,
            @RequestParam(name = "to",     required = false) String to
    ) {
        if (username == null || "anonymousUser".equals(username)) {
            throw new IllegalArgumentException("UNAUTHORIZED");
        }

        Long userId = userService.getByUsername(username).getId();
        return statsService.getOverview(userId);
    }

    /**
     * [공개 프로필 통계(타 사용자)]
     * GET /api/v1/stats/users/{username}/overview?period=month|year
     */
    @GetMapping("/users/{username}/overview")
    public StatsOverviewDto getUserOverview(
            @PathVariable String username,
            @RequestParam(name = "period", required = false) String period,
            @RequestParam(name = "from", required = false) String from,
            @RequestParam(name = "to", required = false) String to
    ) {
        Long targetUserId = userService.getByUsername(username).getId();
        return statsService.getOverview(targetUserId);
    }

    // ======================= 별점 히스토그램 =======================

    /**
     * [내 별점 분포]
     * GET /api/v1/stats/me/stars
     */
    @GetMapping("/me/stars")
    public RatingHistogramDto getMyStarsHistogram(
            @AuthenticationPrincipal String username
    ) {
        if (username == null || "anonymousUser".equals(username)) {
            throw new IllegalArgumentException("UNAUTHORIZED");
        }

        Long userId = userService.getByUsername(username).getId();
        return statsService.getRatingHistogram(userId);
    }

    /**
     * [내가 매긴 별점별 책 목록]
     * GET /api/v1/stats/me/stars/books?rating=1~5&page=0&size=20
     */
    @GetMapping("/me/stars/books")
    public Page<BookCardDto> getMyBooksByRating(
            @AuthenticationPrincipal String username,
            @RequestParam("rating") Double rating,
            org.springframework.data.domain.Pageable pageable
    ) {
        if (username == null || "anonymousUser".equals(username)) {
            throw new IllegalArgumentException("UNAUTHORIZED");
        }
        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("rating 은 1~5 사이여야 합니다.");
        }

        Long userId = userService.getByUsername(username).getId();
        return statsService.getMyBooksByRating(userId, rating, pageable);
    }


    // ======================= 타임라인 =======================

    /**
     * [내 타임라인]
     * GET /api/v1/stats/me/timeline?granularity=month|year&from=YYYY-MM&to=YYYY-MM
     */
    @GetMapping("/me/timeline")
    public Object getMyTimeline(
            @AuthenticationPrincipal String username,
            @RequestParam(name = "granularity", defaultValue = "month") String granularity,
            @RequestParam(name = "from") String from,
            @RequestParam(name = "to", required = false) String to
    ) {
        if (username == null || "anonymousUser".equals(username)) {
            throw new IllegalArgumentException("UNAUTHORIZED");
        }

        Long userId = userService.getByUsername(username).getId();
        int year = parseYearOrDefault(from);

        if ("year".equalsIgnoreCase(granularity)) {
            return statsService.getYearlySummary(userId, year);
        }
        return statsService.getMonthlySeries(userId, year);
    }

    // ======================= 상위 작가 =======================

    /**
     * [내 상위 작가]
     * GET /api/v1/stats/me/authors?top=10
     */
    @GetMapping("/me/authors")
    public TopAuthorsDto getMyTopAuthors(
            @AuthenticationPrincipal String username,
            @RequestParam(name = "top", defaultValue = "5") int top
    ) {
        if (username == null || "anonymousUser".equals(username)) {
            throw new IllegalArgumentException("UNAUTHORIZED");
        }

        if (top <= 0) {
            top = 5;
        }

        Long userId = userService.getByUsername(username).getId();
        return statsService.getTopAuthors(userId, top);
    }

    /**
     * [자주 읽은 저자별 책 목록]
     * GET /api/v1/stats/me/authors/books?author=...
     *
     * - 로그인한 사용자가 해당 author 의 책 중 리뷰한 도서들 리스트
     */
    @GetMapping("/me/authors/books")
    public Page<BookCardDto> getMyBooksByAuthor(
            @AuthenticationPrincipal String username,
            @RequestParam("author") String author,
            org.springframework.data.domain.Pageable pageable
    ) {
        if (username == null || "anonymousUser".equals(username)) {
            throw new IllegalArgumentException("UNAUTHORIZED");
        }

        Long userId = userService.getByUsername(username).getId();
        return statsService.getMyBooksByAuthor(userId, author, pageable);
    }

    // ======================= 카테고리 분포 =======================

    /**
     * [내 카테고리 분포]
     * GET /api/v1/stats/me/categories
     */
    @GetMapping("/me/categories")
    public List<Object> getMyCategoryShare(
            @AuthenticationPrincipal String username
    ) {
        if (username == null || "anonymousUser".equals(username)) {
            throw new IllegalArgumentException("UNAUTHORIZED");
        }

        Long userId = userService.getByUsername(username).getId();
        return List.of(); // TODO: 카테고리 통계 구현 시 교체
    }

    // ======================= 목표/진행률 =======================

    /**
     * [이번달/올해 목표 및 진행률 조회]
     * GET /api/v1/stats/me/goals?period=month|year
     */
    @GetMapping("/me/goals")
    public GoalProgressDto getMyGoals(
            @AuthenticationPrincipal String username,
            @RequestParam(name = "period", defaultValue = "month") String period
    ) {
        if (username == null || "anonymousUser".equals(username)) {
            throw new IllegalArgumentException("UNAUTHORIZED");
        }

        Long userId = userService.getByUsername(username).getId();

        YearMonth now = YearMonth.now();
        YearMonth targetYm;

        if ("year".equalsIgnoreCase(period)) {
            targetYm = YearMonth.of(now.getYear(), 1);
        } else {
            targetYm = now;
        }

        return statsService.getGoalProgress(userId, targetYm);
    }

    // ======================= 헬퍼 =======================

    /**
     * "YYYY" 또는 "YYYY-MM" 형태 문자열에서 연도만 파싱.
     * 값이 비어있거나 형식이 이상하면 현재 연도를 사용한다.
     */
    private int parseYearOrDefault(String value) {
        if (value == null || value.isBlank()) {
            return YearMonth.now().getYear();
        }
        String yearPart = value.trim();
        if (yearPart.length() >= 4) {
            yearPart = yearPart.substring(0, 4);
        }
        try {
            return Integer.parseInt(yearPart);
        } catch (NumberFormatException ex) {
            return YearMonth.now().getYear();
        }
    }
}