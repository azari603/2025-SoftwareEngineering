// src/main/java/com/cheack/softwareengineering/controller/NotificationController.java
package com.cheack.softwareengineering.controller;

import com.cheack.softwareengineering.dto.NotificationDto;
import com.cheack.softwareengineering.dto.UserDto;
import com.cheack.softwareengineering.service.NotificationService;
import com.cheack.softwareengineering.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final UserService userService;

    /**
     * 현재 로그인 유저 ID 조회
     * - JwtAuthenticationFilter 가 principal 에 username 을 넣고 있다고 가정
     */
    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getPrincipal())) {
            throw new IllegalStateException("UNAUTHORIZED");
        }

        String username = String.valueOf(authentication.getPrincipal());
        UserDto user = userService.getByUsername(username);
        return user.getId();
    }

    /**
     * 알림 목록 조회
     * GET /api/v1/notifications?page,size,sort
     */
    @GetMapping
    public Page<NotificationDto> getNotifications(Pageable pageable) {
        Long userId = getCurrentUserId();
        return notificationService.getList(userId, pageable);
    }

    /**
     * 읽지 않은 알림 개수
     * GET /api/v1/notifications/unread-count
     */
    @GetMapping("/unread-count")
    public Map<String, Long> getUnreadCount() {
        Long userId = getCurrentUserId();
        long count = notificationService.getUnreadCount(userId);
        return Map.of("unreadCount", count);
    }

    /**
     * 단건 읽음 처리
     * PATCH /api/v1/notifications/{id}/read
     */
    @PatchMapping("/{id}/read")
    public ResponseEntity<Void> markRead(@PathVariable Long id) {
        Long userId = getCurrentUserId();
        notificationService.markRead(userId, id);
        return ResponseEntity.noContent().build();
    }

    /**
     * 전체 읽음 처리
     * PATCH /api/v1/notifications/read-all
     */
    @PatchMapping("/read-all")
    public ResponseEntity<Void> markAllRead() {
        Long userId = getCurrentUserId();
        notificationService.markAllRead(userId);
        return ResponseEntity.noContent().build();
    }

    /**
     * 단건 삭제
     * DELETE /api/v1/notifications/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        Long userId = getCurrentUserId();
        notificationService.delete(userId, id);
        return ResponseEntity.noContent().build();
    }

    /**
     * 클릭 트래킹(선택)
     * POST /api/v1/notifications/{id}/click
     * 지금은 read=true 처리만 하고 204 반환
     */
    @PostMapping("/{id}/click")
    public ResponseEntity<Void> click(@PathVariable Long id) {
        Long userId = getCurrentUserId();
        notificationService.markRead(userId, id);
        return ResponseEntity.noContent().build();
    }
}