// src/main/java/com/cheack/softwareengineering/entity/ReviewLike.java
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
        name = "review_likes",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_review_like_user_review",
                        columnNames = {"user_id", "review_id"}
                )
        },
        indexes = {
                @Index(name = "idx_review_likes_user", columnList = "user_id"),
                @Index(name = "idx_review_likes_review", columnList = "review_id"),
                @Index(name = "idx_review_likes_liked_at", columnList = "liked_at")
        }
)
public class ReviewLike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_like_id")
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "review_id", nullable = false)
    private Long reviewId;

    /**
     * 좋아요를 누른 시각
     * - 정렬(sort=likedAt,desc) / 메서드 이름 OrderByLikedAtDesc 에서 사용
     */
    @Column(name = "liked_at", nullable = false, updatable = false)
    private LocalDateTime likedAt;

    @PrePersist
    protected void onCreate() {
        if (likedAt == null) {
            likedAt = LocalDateTime.now();
        }
    }
}