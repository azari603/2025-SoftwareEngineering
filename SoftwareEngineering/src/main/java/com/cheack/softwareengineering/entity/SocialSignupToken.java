// src/main/java/com/cheack/softwareengineering/entity/SocialSignupToken.java
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
@Table(name = "social_signup_token")
public class SocialSignupToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** GOOGLE / KAKAO / NAVER / LOCAL 등 */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ProviderType provider;

    /** 각 플랫폼의 고유 ID (sub, id 등) */
    @Column(nullable = false, length = 100)
    private String providerId;

    /** 소셜에서 가져온 이메일 */
    @Column(nullable = false, length = 255)
    private String email;

    /** FE에 넘겨줄 일회성 가입 토큰 (UUID 등) */
    @Column(nullable = false, unique = true, length = 200)
    private String token;

    /** 만료 시각 */
    @Column(nullable = false)
    private LocalDateTime expiresAt;

    /** 이미 사용되었는지 여부 */
    @Column(nullable = false)
    private boolean used;

    /** 생성 시각 (옵션) */
    @Column(nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}