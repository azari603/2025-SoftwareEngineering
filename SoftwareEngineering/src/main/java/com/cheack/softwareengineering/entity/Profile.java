package com.cheack.softwareengineering.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "profiles")
public class Profile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "profile_id")
    private Long id;

    /**
     * User 테이블의 PK
     */
    @Column(name = "user_id", nullable = false, unique = true)
    private Long userId;

    @Column(name = "user_image", length = 500)
    private String userImage;

    @Column(name = "background_image", length = 500)
    private String backgroundImage;

    /**
     * 지금까지 읽은 책 권수(캐시용으로 쓸 수도 있고, 그냥 통계용 필드로 둘 수도 있음)
     */
    @Column(name = "read_book")
    private Long readBook;

    @Column(name = "intro", length = 1000)
    private String intro;

    @Column(name = "monthly_goal")
    private Integer monthlyGoal;
}
