package com.cheack.softwareengineering.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "이메일은 필수입니다.")
    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @NotBlank(message = "비밀번호는 필수입니다.")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\\\d)(?=.*[~!@#$%^&*+])[A-Za-z\\\\d~!@#$%^&*+]{8,20}$", message = "비밀번호는 8~16자 영문 대 소문자, 숫자, 특수문자가 적어도 하나 존재해야 합니다.")
    @Column(nullable = false)
    private String password;

    @NotBlank(message = "아이디는 필수입니다.")
    @Size(min = 2, max = 10, message = "아이디는 2자 이상 10자 이하입니다.")
    @Column(nullable = false, unique = true, length = 10)
    private String username;

    @Column(nullable = false)
    @Builder.Default
    private String nickname = "익명의 책벌레"; // 이부분은 id와

    @Column(name = "profile_image_url")
    private String profileImageUrl;

    @Column(columnDefinition = "TEXT")
    private String introduction;

    @Min(value = 0, message = "목표는 0권 이상이어야 합니다")
    @Max(value = 100, message = "목표는 100권 이하여야 합니다")
    @Column(name = "monthly_goal")
    @Builder.Default
    private Integer monthlyGoal = 0;

    @NotNull(message = "계정 활성 상태는 필수입니다")
    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist // PrePersist: 엔티티가 영속화 되기전에 실행되어야 하는 함수에 붙은 annotation
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}