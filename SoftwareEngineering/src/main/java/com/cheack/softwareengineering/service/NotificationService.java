package com.cheack.softwareengineering.service;

import com.cheack.softwareengineering.dto.NotificationDto;
import com.cheack.softwareengineering.entity.Notification;
import com.cheack.softwareengineering.entity.NotificationType;
import com.cheack.softwareengineering.repository.NotificationRepository;
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
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;     // 지금은 사용 안 해도 일단 의존성만
    private final ReviewRepository reviewRepository; // 마찬가지로 확장용

    /**
     * 알림 생성
     */
    @Transactional
    public void create(Long receiverId,
                       Long actorId,
                       NotificationType type,
                       String targetUrl,
                       String content,
                       Long reviewId) {

        Notification notification = Notification.builder()
                .receiverId(receiverId)
                .actorId(actorId)
                .reviewId(reviewId)
                .type(type)
                .targetUrl(targetUrl)
                .content(content)
                .isRead(false)
                .createdAt(LocalDateTime.now())
                .build();

        notificationRepository.save(notification);
    }

    /**
     * 알림 목록 조회 (최신 순)
     */
    public Page<NotificationDto> getList(Long userId, Pageable pageable) {
        return notificationRepository
                .findByReceiverIdOrderByCreatedAtDesc(userId, pageable)
                .map(NotificationDto::from);
    }

    /**
     * 읽지 않은 알림 개수
     */
    public long getUnreadCount(Long userId) {
        return notificationRepository.countByReceiverIdAndIsReadFalse(userId);
    }

    /**
     * 단건 읽음 처리
     */
    @Transactional
    public void markRead(Long userId, Long notificationId) {
        Notification notification = notificationRepository
                .findByIdAndReceiverId(notificationId, userId)
                .orElseThrow(() -> new IllegalArgumentException("Notification not found"));

        if (!notification.isRead()) {
            notification.markRead();
        }
    }

    /**
     * 전체 읽음 처리
     */
    @Transactional
    public void markAllRead(Long userId) {
        notificationRepository
                .findByReceiverIdOrderByCreatedAtDesc(userId, Pageable.unpaged())
                .forEach(n -> {
                    if (!n.isRead()) {
                        n.markRead();
                    }
                });
    }

    /**
     * 단건 삭제
     */
    @Transactional
    public void delete(Long userId, Long notificationId) {
        notificationRepository.deleteByIdAndReceiverId(notificationId, userId);
    }
}