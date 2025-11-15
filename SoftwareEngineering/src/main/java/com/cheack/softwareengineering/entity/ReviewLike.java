package com.cheack.softwareengineering.entity;

import jakarta.persistence.*;
import lombok.*;

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
                        name = "uk_review_likes_review_user",
                        columnNames = {"review_id", "user_id"}
                )
        },
        indexes = {
                @Index(name = "idx_review_likes_review", columnList = "review_id"),
                @Index(name = "idx_review_likes_user", columnList = "user_id")
        }
)
public class ReviewLike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "like_id")
    private Long id;

    @Column(name = "review_id", nullable = false)
    private Long reviewId;

    @Column(name = "user_id", nullable = false)
    private Long userId;
}
