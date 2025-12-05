package com.cheack.softwareengineering.repository;

import com.cheack.softwareengineering.entity.ReadingStatus;
import com.cheack.softwareengineering.entity.ReadingStatusType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReadingStatusRepository extends JpaRepository<ReadingStatus, Long> {

    // + findByUserIdAndBookId(userId : Long, bookId : Long) : Optional<ReadingStatus>
    Optional<ReadingStatus> findByUserIdAndBookId(Long userId, Long bookId);

    // + existsByUserIdAndBookId(userId : Long, bookId : Long) : boolean
    boolean existsByUserIdAndBookId(Long userId, Long bookId);

    // + deleteByUserIdAndBookId(userId : Long, bookId : Long) : void
    void deleteByUserIdAndBookId(Long userId, Long bookId);

    // + findByUserIdAndStatus(userId : Long, status : ReadingStatusType, p : Pageable) : Page<ReadingStatus>
    Page<ReadingStatus> findByUserIdAndStatus(Long userId, ReadingStatusType status, Pageable pageable);

    // + countByUserIdAndStatus(userId : Long, status : ReadingStatusType) : Long
    long countByUserIdAndStatus(Long userId, ReadingStatusType status);

}
