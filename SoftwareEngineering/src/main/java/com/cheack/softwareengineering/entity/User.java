package com.cheack.softwareengineering.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@Setter // 서비스 레이어에서 상태 세팅/변경
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true) // 네 코드에서 builder() 사용하므로 유지
@Entity
@Table(
        name = "users",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_users_username", columnNames = "username"),
                @UniqueConstraint(name = "uk_users_email", columnNames = "email")
        },
        indexes = {
                @Index(name = "idx_users_email", columnList = "email"),
                @Index(name = "idx_users_provider_providerId", columnList = "provider,providerId")
        }
)
@EntityListeners(AuditingEntityListener.class)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 아이디(@로 시작, 로그인용) */
    @Column(nullable = false, length = 20)
    private String username;

    /** 이메일(아이디/비번 찾기, 인증메일) */
    @Column(nullable = false, length = 255)
    private String email;

    /** BCrypt 해시(소셜 전용 계정은 null 가능) */
    @Column(length = 100)
    private String password;

    /** 닉네임 – 가입 시 username과 동일하게 세팅(서비스에서 처리) */
    @Column(nullable = false, length = 20)
    private String nickname;

    /** 이메일 인증 여부 (네 코드에서 getIsEmailVerified()/setIsEmailVerified()를 쓰고 있음) */
    @Builder.Default
    @Column(nullable = false)
    private boolean emailVerified = false;

    /** 소셜 제공자/식별자 */
    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ProviderType provider = ProviderType.LOCAL;

    @Column(length = 100)
    private String providerId; // 소셜 고유키

    /** 계정 상태 */
    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private UserStatus status = UserStatus.ACTIVE;

    /** 마지막 로그인 시각 */
    private LocalDateTime lastLoginAt;

    /** 생성/수정 시간 */
    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    /** 네 코드: user.getIsEmailVerified() 사용 → 명시적 게터 제공 */
    public boolean getIsEmailVerified() {
        return this.emailVerified;
    }

    /** 네 코드: user.setIsEmailVerified(true) 사용 → 명시적 세터 제공 */
    public void setIsEmailVerified(boolean verified) {
        this.emailVerified = verified;
    }

    /** 네 코드: user.getSocialType() 사용 → provider로부터 유도 */
    public String getSocialType() {
        return (this.provider != null ? this.provider.name() : null);
    }
}
