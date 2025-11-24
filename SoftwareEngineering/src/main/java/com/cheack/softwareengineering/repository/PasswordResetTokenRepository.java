package com.cheack.softwareengineering.repository;

import com.cheack.softwareengineering.entity.PasswordResetToken;
import com.cheack.softwareengineering.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {

    Optional<PasswordResetToken> findByTokenAndUsedFalse(String token);

    Optional<PasswordResetToken> findTopByUserOrderByCreatedAtDesc(User user);
}
