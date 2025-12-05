package com.cheack.softwareengineering.repository;

import com.cheack.softwareengineering.entity.EmailVerificationToken;
import com.cheack.softwareengineering.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmailVerificationTokenRepository extends JpaRepository<EmailVerificationToken, Long> {

    Optional<EmailVerificationToken> findByTokenAndUsedFalse(String token);

    Optional<EmailVerificationToken> findTopByUserOrderByCreatedAtDesc(User user);
}