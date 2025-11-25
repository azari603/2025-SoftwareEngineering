package com.cheack.softwareengineering.controller;

import com.cheack.softwareengineering.dto.UserDto;
import com.cheack.softwareengineering.dto.ReadingStatusDto;
import com.cheack.softwareengineering.dto.ReadingStatusSummary;
import com.cheack.softwareengineering.dto.SetStatusRequest;
import com.cheack.softwareengineering.entity.ReadingStatusType;
import com.cheack.softwareengineering.service.ReadingStatusService;
import com.cheack.softwareengineering.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * 내 서재(ReadingStatus) 관련 컨트롤러.
 *
 * Base URL (API 설계 기준): /api/v1/library
 *
 * - PUT    /api/v1/library/books/{bookId}/status      : 읽기 상태 설정
 * - DELETE /api/v1/library/books/{bookId}/status      : 읽기 상태 제거
 * - GET    /api/v1/library/books?status=...           : 특정 상태의 도서 목록 조회
 * - GET    /api/v1/library/books/counts               : 상태별 도서 수 요약 조회
 */
@RestController
@RequestMapping("/api/v1/library")
@RequiredArgsConstructor
public class ReadingStatusController {

    private final UserService userService;
    private final ReadingStatusService readingStatusService;

    /**
     * [상태 설정]
     * PUT /api/v1/library/books/{bookId}/status
     *
     * body: { "status": "READING" } 와 같이 전달.
     * 로그인 사용자는 SecurityContext 의 principal 에서 id 를 꺼낸다고 가정
     * (principal 에 getId() 가 있다고 보고 expression = "id" 사용).
     */
    @PutMapping("/books/{bookId}/status")
    public void setStatus(
            @AuthenticationPrincipal String username,
            @PathVariable Long bookId,
            @RequestBody @Valid SetStatusRequest request
    ) {
        UserDto user = userService.getByUsername(username);
        Long userId = user.getId();

        readingStatusService.setStatus(userId, bookId, request.getStatus());
    }

    /**
     * [상태 제거]
     * DELETE /api/v1/library/books/{bookId}/status
     */
    @DeleteMapping("/books/{bookId}/status")
    public void clearStatus(
            @AuthenticationPrincipal String username,
            @PathVariable Long bookId
    ) {
        UserDto user = userService.getByUsername(username);
        Long userId = user.getId();

        readingStatusService.clearStatus(userId, bookId);
    }

    /**
     * [상태별 내 서재 조회]
     * GET /api/v1/library/books?status=READING&page=0&size=20
     *
     * Pageable 은 spring-data-web 지원(page, size, sort 파라미터) 그대로 사용.
     */
    @GetMapping("/books")
    public Page<ReadingStatusDto> listByStatus(
            @AuthenticationPrincipal String username,
            @RequestParam("status") ReadingStatusType status,
            Pageable pageable
    ) {
        if (username == null || "anonymousUser".equals(username)) {
            throw new IllegalArgumentException("UNAUTHORIZED");
        }

        UserDto user = userService.getByUsername(username);
        Long userId = user.getId();

        return readingStatusService.getByStatus(userId, status, pageable);
    }

    /**
     * [상태별 도서 수 요약]
     * GET /api/v1/library/books/counts
     *
     * ReadingStatusSummary:
     *  - totalCount
     *  - countsByStatus (READING / COMPLETED / WANT_TO_READ 등 enum 별 카운트 맵)
     */
    @GetMapping("/books/counts")
    public ReadingStatusSummary counts(
            @AuthenticationPrincipal String username
    ) {
        if (username == null || "anonymousUser".equals(username)) {
            throw new IllegalArgumentException("UNAUTHORIZED");
        }
        UserDto user = userService.getByUsername(username);
        Long userId = user.getId();

        return readingStatusService.getSummary(userId);
    }
}
