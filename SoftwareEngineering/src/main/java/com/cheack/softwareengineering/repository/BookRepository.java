package com.cheack.softwareengineering.repository;

import com.cheack.softwareengineering.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Book 엔티티용 JPA 리포지토리
 */
public interface BookRepository extends JpaRepository<Book, Long> {

    // ISBN 으로 단건 조회
    Optional<Book> findByIsbn(String isbn);

    // 제목이나 저자에 키워드가 포함된 책 검색
    Page<Book> findByNameContainingIgnoreCaseOrAuthorContainingIgnoreCase(
            String nameKeyword,
            String authorKeyword,
            Pageable pageable
    );
}
