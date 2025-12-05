// src/main/java/com/cheack/softwareengineering/repository/SocialSignupTokenRepository.java
package com.cheack.softwareengineering.repository;

import com.cheack.softwareengineering.entity.SocialSignupToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SocialSignupTokenRepository extends JpaRepository<SocialSignupToken, Long> {

    Optional<SocialSignupToken> findByToken(String token);
}