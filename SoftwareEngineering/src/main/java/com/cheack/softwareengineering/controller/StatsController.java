// src/main/java/com/cheack/softwareengineering/controller/StatsController.java
package com.cheack.softwareengineering.controller;

import com.cheack.softwareengineering.dto.*;
import com.cheack.softwareengineering.service.StatsService;
import com.cheack.softwareengineering.service.UserService;
import lombok.RequiredArgsConstructor;
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
 *
 * 현재 서비스 계층이 제공하는 메서드에 맞춰,
 * 기간/구간 관련 파라미터는 일부만 사용하거나 확장 여지로 두고 있다.
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
     *
     * 현재 구현:
     *  - StatsService.getOverview(userId) 결과(전체 개요)를 그대로 반환
     *  - period/from/to 는 향후 범위별 통계를 지원하기 위한 확장용 파라미터
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
     *
     * 현재는 공개 범위 체크 로직 없이, 해당 사용자의 전체 개요 통계를 내려준다.
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
     *
     * 응답: 1~5점 카운트를 담은 RatingHistogramDto
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

    // ======================= 타임라인 =======================

    /**
     * [내 타임라인]
     * GET /api/v1/stats/me/timeline?granularity=month|year&from=YYYY-MM&to=YYYY-MM
     *
     * 구현:
     *  - granularity=month  -> 해당 연도의 MonthlySeriesDto 반환
     *  - granularity=year   -> 해당 연도의 YearlySummaryDto 반환
     *
     * from/to 는 설계상 구간이지만, 현재 서비스는 "연도 단위"만 지원하므로
     * from 의 연도만 사용하고 to 는 무시한다.
     *
     * 반환 타입을 Object 로 두어, month/year 에 따라 서로 다른 DTO를 내려줄 수 있게 했다.
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
     *
     * 응답: TopAuthorsDto (상위 작가 목록)
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

    // ======================= 카테고리 분포 =======================

    /**
     * [내 카테고리 분포]
     * GET /api/v1/stats/me/categories
     *
     * 아직 카테고리 통계를 계산하는 서비스 메서드가 없으므로,
     * 일단 빈 리스트를 반환해 두고, 나중에 StatsService 확장 시 구현한다.
     */
    @GetMapping("/me/categories")
    public List<Object> getMyCategoryShare(
            @AuthenticationPrincipal String username
    ) {
        if (username == null || "anonymousUser".equals(username)) {
            throw new IllegalArgumentException("UNAUTHORIZED");
        }

        // TODO: userId로 카테고리 통계 계산할 때 사용
        Long userId = userService.getByUsername(username).getId();

        return List.of(); // 지금은 기존대로 빈 리스트
    }

    // ======================= 목표/진행률 =======================

    /**
     * [이번달/올해 목표 및 진행률 조회]
     * GET /api/v1/stats/me/goals?period=month|year
     *
     * 현재 StatsService는 YearMonth 단위 목표만 지원하므로,
     *  - period=month (기본): 이번 달 YearMonth 기준으로 조회
     *  - period=year       : 현 연도의 1월을 대표 월로 조회 (임시 구현)
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
