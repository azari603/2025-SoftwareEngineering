package com.cheack.softwareengineering.service;

import com.cheack.softwareengineering.dto.NotificationDto;
import com.cheack.softwareengineering.entity.Notification;
import com.cheack.softwareengineering.entity.NotificationType;
import com.cheack.softwareengineering.entity.Review;
import com.cheack.softwareengineering.entity.User;
import com.cheack.softwareengineering.repository.NotificationRepository;
import com.cheack.softwareengineering.repository.ReviewRepository;
import com.cheack.softwareengineering.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final ReviewRepository reviewRepository;

    @Transactional
    public void create(Long receiverId,
                       Long actorId,
                       NotificationType type,
                       String targetUrl,
                       String content,
                       Long reviewId) {

        String actorUsername = null;
        String actorNickname = null;
        String reviewTitle = null;

        Optional<User> actorOpt = Optional.ofNullable(actorId)
                .flatMap(userRepository::findById);
        if (actorOpt.isPresent()) {
            actorUsername = actorOpt.get().getUsername();
            actorNickname = actorOpt.get().getNickname();
        }

        Optional<Review> reviewOpt = Optional.ofNullable(reviewId)
                .flatMap(reviewRepository::findById);
        if (reviewOpt.isPresent()) {
            reviewTitle = reviewOpt.get().getTitle();
        }

        String finalContent = content;
        String displayName = (actorNickname != null && !actorNickname.isBlank())
                ? actorNickname
                : actorUsername;

        if (type == NotificationType.REVIEW_COMMENT) {
            String title = Objects.toString(reviewTitle, "서평");
            finalContent = displayName + "님이 '" + title + "'에 댓글을 남겼습니다.";
        } else if (type == NotificationType.REVIEW_LIKE) {
            String title = Objects.toString(reviewTitle, "서평");
            finalContent = displayName + "님이 '" + title + "'에 좋아요를 눌렀습니다.";
        } else if (type == NotificationType.FOLLOW) {
            finalContent = displayName + "님이 회원님을 팔로우하기 시작했습니다.";
        } else if (finalContent == null || finalContent.isBlank()) {
            finalContent = "새 알림이 도착했습니다.";
        }

        Notification notification = Notification.builder()
                .receiverId(receiverId)
                .actorId(actorId)
                .reviewId(reviewId)
                .type(type)
                .targetUrl(targetUrl)
                .content(finalContent)
                .isRead(false)
                .createdAt(LocalDateTime.now())
                .actorUsername(actorUsername)
                .actorNickname(actorNickname)
                .reviewTitle(reviewTitle)
                .build();

        notificationRepository.save(notification);
    }

    public Page<NotificationDto> getList(Long userId, Pageable pageable) {
        return notificationRepository
                .findByReceiverIdOrderByCreatedAtDesc(userId, pageable)
                .map(NotificationDto::from);
    }

    public long getUnreadCount(Long userId) {
        return notificationRepository.countByReceiverIdAndIsReadFalse(userId);
    }

    @Transactional
    public void markRead(Long userId, Long notificationId) {
        Notification notification = notificationRepository
                .findByIdAndReceiverId(notificationId, userId)
                .orElseThrow(() -> new IllegalArgumentException("Notification not found"));

        if (!notification.isRead()) {
            notification.markRead();
        }
    }

    @Transactional
    public void markAllRead(Long userId) {
        notificationRepository
                .findByReceiverIdOrderByCreatedAtDesc(userId, Pageable.unpaged())
                .forEach(n -> {
                    if (!n.isRead()) n.markRead();
                });
    }

    @Transactional
    public void delete(Long userId, Long notificationId) {
        notificationRepository.deleteByIdAndReceiverId(notificationId, userId);
    }
}