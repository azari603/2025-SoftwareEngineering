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
                @Index(
                        name = "idx_notifications_receiver_created_at",
                        columnList = "receiver_id, created_at"
                ),
                @Index(
                        name = "idx_notifications_receiver_read",
                        columnList = "receiver_id, is_read"
                )
        }
)
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id")
    private Long id;

    /**
     * 알림을 받는 사람(수신자)
     */
    @Column(name = "receiver_id", nullable = false)
    private Long receiverId;

    /**
     * 알림을 발생시킨 사람(행위자, 예: 팔로우 한 사람, 댓글 작성자)
     */
    @Column(name = "actor_id", nullable = false)
    private Long actorId;

    /**
     * 리뷰 관련 알림일 때 연결되는 리뷰 ID (없으면 null)
     */
    @Column(name = "review_id")
    private Long reviewId;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 50)
    private NotificationType type;

    /**
     * 프론트에서 이동할 딥링크 형태의 URL
     */
    @Column(name = "target_url", length = 500)
    private String targetUrl;

    /**
     * 화면에 그대로 보여줄 메시지
     */
    @Column(name = "content", nullable = false, length = 1000)
    private String content;

    /**
     * 읽음 여부
     */
    @Column(name = "is_read", nullable = false)
    private boolean isRead;

    /**
     * 생성 시각
     */
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public void markRead() {
        this.isRead = true;
    }
}