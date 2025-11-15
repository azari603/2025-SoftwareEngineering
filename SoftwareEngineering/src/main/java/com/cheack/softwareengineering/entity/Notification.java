package com.cheack.softwareengineering.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(
        name = "notifications",
        indexes = {
                @Index(name = "idx_notifications_user", columnList = "user_id"),
                @Index(name = "idx_notifications_receiver", columnList = "receiver_id"),
                @Index(name = "idx_notifications_review", columnList = "review_id"),
                @Index(name = "idx_notifications_is_read", columnList = "is_read")
        }
)
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id")
    private Long id;

    /**
     * 알림을 발생시킨 주체(보낸 사람) – ERD 기준 user_id
     */
    @Column(name = "user_id", nullable = false)
    private Long userId;

    /**
     * 알림을 받는 사람(수신자)
     */
    @Column(name = "receiver_id", nullable = false)
    private Long receiverId;

    /**
     * 해당 알림이 연관된 리뷰가 있다면 설정(없으면 null)
     */
    @Column(name = "review_id")
    private Long reviewId;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 50)
    private NotificationType type;

    @Column(name = "content", nullable = false, length = 500)
    private String content;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "is_read", nullable = false)
    private Boolean isRead;
}
