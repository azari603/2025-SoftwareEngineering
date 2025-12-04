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
                @Index(name = "idx_notifications_receiver_created_at", columnList = "receiver_id, created_at"),
                @Index(name = "idx_notifications_receiver_read", columnList = "receiver_id, is_read")
        }
)
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id")
    private Long id;

    @Column(name = "receiver_id", nullable = false)
    private Long receiverId;

    @Column(name = "actor_id", nullable = false)
    private Long actorId;

    @Column(name = "review_id")
    private Long reviewId;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 50)
    private NotificationType type;

    @Column(name = "target_url", length = 500)
    private String targetUrl;

    @Column(name = "content", nullable = false, length = 1000)
    private String content;

    @Column(name = "is_read", nullable = false)
    private boolean isRead;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "actor_username", length = 20)
    private String actorUsername;   // 스냅샷

    @Column(name = "actor_nickname", length = 20)
    private String actorNickname;   // 스냅샷

    @Column(name = "review_title", length = 300)
    private String reviewTitle;     // 스냅샷

    public void markRead() {
        this.isRead = true;
    }
}