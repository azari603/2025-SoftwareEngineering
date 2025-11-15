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
        name = "reading_status",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_reading_status_user_book",
                        columnNames = {"user_id", "book_id"}
                )
        },
        indexes = {
                @Index(name = "idx_reading_status_user", columnList = "user_id"),
                @Index(name = "idx_reading_status_book", columnList = "book_id"),
                @Index(name = "idx_reading_status_status", columnList = "status")
        }
)
public class ReadingStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reading_status_id")
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "book_id", nullable = false)
    private Long bookId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private ReadingStatusType status;
}
