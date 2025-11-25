package com.cheack.softwareengineering.controller;

import com.cheack.softwareengineering.dto.CommentCreateRequest;
import com.cheack.softwareengineering.dto.UserDto;
import com.cheack.softwareengineering.dto.CommentDto;
import com.cheack.softwareengineering.dto.CommentIdResponse;
import com.cheack.softwareengineering.dto.CommentUpdateRequest;
import com.cheack.softwareengineering.service.CommentService;
import com.cheack.softwareengineering.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * Comment & Like API v1 중 댓글 부분
 *
 * Base(댓글): /api/v1/reviews/{reviewId}/comments
 *
 * - GET    /api/v1/reviews/{reviewId}/comments
 * - POST   /api/v1/reviews/{reviewId}/comments
 * - PATCH  /api/v1/comments/{commentId}
 * - DELETE /api/v1/comments/{commentId}
 */
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;
    private final UserService userService;

    /**
     * [댓글 목록 조회]
     * GET /api/v1/reviews/{reviewId}/comments?page,size,sort
     *
     * 로그인 안 한 경우도 호출 가능하게 할 거면
     * @AuthenticationPrincipal 은 null 허용된다.
     */
    @GetMapping("/reviews/{reviewId}/comments")
    public ResponseEntity<Page<CommentDto>> getComments(
            @PathVariable Long reviewId,
            Pageable pageable,
            @AuthenticationPrincipal String principalUsername
    ) {
        Long userId = null;
        if (principalUsername != null && !"anonymousUser".equals(principalUsername)) {
            UserDto user = userService.getByUsername(principalUsername);
            userId = user.getId();
        }

        Page<CommentDto> page = commentService.getByReview(reviewId, userId, pageable);
        return ResponseEntity.ok(page);
    }

    /**
     * [댓글 작성]
     * POST /api/v1/reviews/{reviewId}/comments
     *
     * Auth 필요.
     * body: { "text": "내용" }
     */
    @PostMapping("/reviews/{reviewId}/comments")
    public ResponseEntity<CommentIdResponse> addComment(
            @PathVariable Long reviewId,
            @RequestBody CommentCreateRequest request,
            @AuthenticationPrincipal String principalUsername
    ) {
        if (principalUsername == null || "anonymousUser".equals(principalUsername)) {
            throw new IllegalArgumentException("UNAUTHORIZED");
        }

        UserDto user = userService.getByUsername(principalUsername);
        Long userId = user.getId();

        Long createdId = commentService.add(userId, reviewId, request.getText());
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new CommentIdResponse(createdId));
    }

    /**
     * [댓글 수정]
     * PATCH /api/v1/comments/{commentId}
     *
     * Auth 필요, 본인만 수정 가능.
     * body: { "text": "수정내용" }
     */
    @PatchMapping("/comments/{commentId}")
    public ResponseEntity<Void> editComment(
            @PathVariable Long commentId,
            @RequestBody CommentUpdateRequest request,
            @AuthenticationPrincipal String principalUsername
    ) {
        if (principalUsername == null || "anonymousUser".equals(principalUsername)) {
            throw new IllegalArgumentException("UNAUTHORIZED");
        }

        UserDto user = userService.getByUsername(principalUsername);
        Long userId = user.getId();

        commentService.edit(userId, commentId, request.getText());
        return ResponseEntity.noContent().build();
    }
    /**
     * [댓글 삭제]
     * DELETE /api/v1/comments/{commentId}
     *
     * Auth 필요, 본인 또는 관리자(서비스단에서 판단)만 삭제 가능.
     */
    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable Long commentId,
            @AuthenticationPrincipal String principalUsername
    ) {
        if (principalUsername == null || "anonymousUser".equals(principalUsername)) {
            throw new IllegalArgumentException("UNAUTHORIZED");
        }

        UserDto user = userService.getByUsername(principalUsername);
        Long userId = user.getId();

        commentService.remove(userId, commentId);
        return ResponseEntity.noContent().build();
    }
}