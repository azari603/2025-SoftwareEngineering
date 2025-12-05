// src/main/java/com/cheack/softwareengineering/repository/BtiResultRepository.java
package com.cheack.softwareengineering.repository;

import com.cheack.softwareengineering.entity.BookBTI;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BtiResultRepository extends JpaRepository<BookBTI, Long> {

    // 한 사용자당 결과 1개만 저장한다고 가정
    Optional<BookBTI> findByUserId(Long userId);
}
