package com.cheack.softwareengineering.repository;

import com.cheack.softwareengineering.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    Page<Notification> findByReceiverIdOrderByCreatedAtDesc(Long receiverId, Pageable pageable);

    long countByReceiverIdAndIsReadFalse(Long receiverId);

    Optional<Notification> findByIdAndReceiverId(Long id, Long receiverId);

    void deleteByIdAndReceiverId(Long id, Long receiverId);
}