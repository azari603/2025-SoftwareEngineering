package com.cheack.softwareengineering.repository;

import com.cheack.softwareengineering.entity.
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // 이메일로 조회
    Optional<User> findByEmail(String email);

    // 으로 조회
    Optional<User> findByUsername(String nickname);

    // 닉네임으로 조회
    Optional<User> findByNickname(String nickname);

    // 이메일 중복체크
    boolean existsByEmail(String email);

    // 닉네임 중복 체크
    boolean existsByNickname(String nickname);

    // 소셜 로그인 사용자 조회
    //Optional<User> findBySocialTypeAndSocialId(SocialType socialType, String socialId);

    // 활성 사용자만 조회
    @Query("SELECT u FROM User u WHERE u.email = :email AND u.isActive = true")
    Optional<User> findActiveUserByEmail(@Param("email") String email);
}