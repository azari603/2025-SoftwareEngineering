// 경로: src/main/java/com/cheack/softwareengineering/service/CommentService.java
package com.cheack.softwareengineering.service;

import com.cheack.softwareengineering.dto.CommentDto;
import com.cheack.softwareengineering.dto.UserMiniDto;
import com.cheack.softwareengineering.entity.Comment;
import com.cheack.softwareengineering.entity.NotificationType;
import com.cheack.softwareengineering.entity.Review;
import com.cheack.softwareengineering.entity.User;
import com.cheack.softwareengineering.repository.CommentRepository;
import com.cheack.softwareengineering.repository.ReviewRepository;
import com.cheack.softwareengineering.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentService {

    private final CommentRepository commentRepository;
    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    // 나중에 ProfileService를 쓰고 싶으면 이렇게 주입해서,
    // Controller에서 CommentDto + Profile까지 묶은 응답 DTO를 새로 만들면 됨.
    // private final ProfileService profileService;

    /**
     * 댓글 작성
     */
    @Transactional
    public Long add(Long userId, Long reviewId, String text) {
        if (text == null || text.isBlank()) {
            throw new IllegalArgumentException("댓글 내용은 비어 있을 수 없습니다.");
            // TODO: 나중에 CustomException + ErrorCode 로 교체
        }

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("Review not found"));

        LocalDateTime now = LocalDateTime.now();

        Comment comment = Comment.builder()
                .reviewId(reviewId)
                .userId(userId)
                .text(text)
                .createdAt(now)
                .updatedAt(now)
                .build();

        Comment saved = commentRepository.save(comment);

        // 리뷰 작성자에게 알림 (자기 글에 단 댓글이면 알림 X)
        if (!review.getUserId().equals(userId)) {
            notificationService.create(
                    review.getUserId(),               // receiverId (리뷰 작성자)
                    userId,                           // actorId   (댓글 단 사람)
                    NotificationType.REVIEW_COMMENT,  // 타입
                    "/reviews/" + reviewId,           // targetUrl
                    "새 댓글이 달렸습니다.",               // content
                    reviewId                          // reviewId
            );
        }

        return saved.getId();
    }

    /**
     * 댓글 수정 (작성자만)
     */
    @Transactional
    public void edit(Long userId, Long commentId, String text) {
        if (text == null || text.isBlank()) {
            throw new IllegalArgumentException("댓글 내용은 비어 있을 수 없습니다.");
        }

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("Comment not found"));

        if (!comment.getUserId().equals(userId)) {
            throw new IllegalArgumentException("본인 댓글만 수정할 수 있습니다.");
        }

        comment.setText(text);
        comment.setUpdatedAt(LocalDateTime.now());
    }

    /**
     * 댓글 삭제 (작성자만)
     */
    @Transactional
    public void remove(Long userId, Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("Comment not found"));

        if (!comment.getUserId().equals(userId)) {
            throw new IllegalArgumentException("본인 댓글만 삭제할 수 있습니다.");
        }

        commentRepository.deleteById(commentId);
    }

    /**
     * 리뷰별 댓글 목록 조회
     * viewerId 를 받아서 mine 플래그를 정확히 채운다.
     *
     * - viewerId == null 이면 전부 mine=false
     * - viewerId != null 이면 comment.userId == viewerId 인 경우에만 mine=true
     */
    public Page<CommentDto> getByReview(Long reviewId, Long viewerId, Pageable pageable) {
        return commentRepository
                .findByReviewIdOrderByCreatedAtDesc(reviewId, pageable)
                .map(comment -> {
                    User user = userRepository.findById(comment.getUserId())
                            .orElse(null);

                    UserMiniDto author = UserMiniDto.from(user);
                    boolean mine = (viewerId != null) && comment.getUserId().equals(viewerId);

                    return CommentDto.from(comment, author, mine);
                });
    }

    /**
     * 리뷰별 댓글 개수
     */
    public long countByReview(Long reviewId) {
        return commentRepository.countByReviewId(reviewId);
    }
}