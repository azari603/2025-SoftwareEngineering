package com.cheack.softwareengineering.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
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
                @Index(name = "idx_reviews_user_deleted", columnList = "user_id, deleted"),
                @Index(name = "idx_reviews_book_visibility_deleted", columnList = "book_id, visibility, deleted")
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

    @Column(name = "title", nullable = false, length = 300)
    private String title;

    @Column(name = "text", nullable = false, length = 4000)
    private String text;

    @Column(name = "star_rating", nullable = false)
    private Double starRating;

    @Enumerated(EnumType.STRING)
    @Column(name = "visibility", nullable = false, length = 20)
    private Visibility visibility;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "finish_date")
    private LocalDate finishDate;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /**
     * 소프트 삭제 플래그
     */
    @Column(name = "deleted", nullable = false)
    private boolean deleted;
}