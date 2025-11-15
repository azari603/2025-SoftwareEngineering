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
        name = "reviews",
        indexes = {
                @Index(name = "idx_reviews_user", columnList = "user_id"),
                @Index(name = "idx_reviews_book", columnList = "book_id")
        }
)
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id")
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "book_id", nullable = false)
    private Long bookId;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "start_date")
    private LocalDateTime startDate;

    @Column(name = "finish_date")
    private LocalDateTime finishDate;

    @Column(name = "star_rating")
    private Double starRating;

    /**
     * true = PUBLIC, false = PRIVATE 와 같이 쓸 수 있음.
     * 나중에 Enum 으로 바꾸고 싶으면 visibilityType 같은 필드 하나 더 두고 마이그레이션 해도 됨.
     */
    @Column(name = "visibility", nullable = false)
    private Boolean visibility;

    @Column(name = "text", columnDefinition = "TEXT")
    private String text;
}
