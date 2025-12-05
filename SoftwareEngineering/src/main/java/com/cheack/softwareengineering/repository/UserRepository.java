package com.cheack.softwareengineering.repository;

import com.cheack.softwareengineering.entity.ProviderType;
import com.cheack.softwareengineering.entity.User;
import com.cheack.softwareengineering.entity.UserStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    Optional<User> findByProviderAndProviderId(ProviderType provider, String providerId);

    Optional<User> findByUsernameAndStatus(String username, UserStatus status);

    Page<User> findByUsernameContainingIgnoreCaseOrNicknameContainingIgnoreCase(
            String usernamePart,
            String nicknamePart,
            Pageable pageable
    );
}
