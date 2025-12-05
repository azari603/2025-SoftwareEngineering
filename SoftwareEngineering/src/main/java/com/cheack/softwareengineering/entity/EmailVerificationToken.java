package com.cheack.softwareengineering.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "email_verification_token",
        indexes = {
                @Index(name = "idx_evt_token", columnList = "token"),
                @Index(name = "idx_evt_user", columnList = "user_id")
        })
@EntityListeners(AuditingEntityListener.class)
public class EmailVerificationToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 대상 사용자 */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false,
            foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private User user;

    /** 검증 토큰 */
    @Column(nullable = false, unique = true, length = 64)
    private String token;

    /** 만료 시각 */
    @Column(nullable = false)
    private LocalDateTime expiresAt;

    /** 사용 여부(단건사용) */
    @Column(nullable = false)
    private boolean used = false;

    /** 생성 시각 */
    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
}