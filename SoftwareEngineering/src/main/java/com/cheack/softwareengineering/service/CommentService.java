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

    @Transactional
    public Long add(Long userId, Long reviewId, String text) {
        if (text == null || text.isBlank()) {
            throw new IllegalArgumentException("댓글 내용은 비어 있을 수 없습니다.");
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

        if (!review.getUserId().equals(userId)) {
            notificationService.create(
                    review.getUserId(),
                    userId,
                    NotificationType.REVIEW_COMMENT,
                    "/reviews/" + reviewId,
                    null,
                    reviewId
            );
        }

        return saved.getId();
    }

    public Page<CommentDto> getByReview(Long reviewId, Long viewerId, Pageable pageable) {
        return commentRepository
                .findByReviewIdOrderByCreatedAtDesc(reviewId, pageable)
                .map(comment -> {
                    User user = userRepository.findById(comment.getUserId()).orElse(null);
                    UserMiniDto author = UserMiniDto.from(user);
                    boolean mine = (viewerId != null) && comment.getUserId().equals(viewerId);
                    return CommentDto.from(comment, author, mine);
                });
    }

    public long countByReview(Long reviewId) {
        return commentRepository.countByReviewId(reviewId);
    }

    @Transactional
    public void edit(Long userId, Long commentId, String text) {
        if (text == null || text.isBlank()) {
            throw new IllegalArgumentException("댓글 내용은 비어 있을 수 없습니다.");
        }
        var comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("Comment not found"));
        if (!comment.getUserId().equals(userId)) {
            throw new IllegalArgumentException("본인 댓글만 수정할 수 있습니다.");
        }
        comment.setText(text);
        comment.setUpdatedAt(LocalDateTime.now());
    }

    @Transactional
    public void remove(Long userId, Long commentId) {
        var comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("Comment not found"));
        if (!comment.getUserId().equals(userId)) {
            throw new IllegalArgumentException("본인 댓글만 삭제할 수 있습니다.");
        }
        commentRepository.deleteById(commentId);
    }
}