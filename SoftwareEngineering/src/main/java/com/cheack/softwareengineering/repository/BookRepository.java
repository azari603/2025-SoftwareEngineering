package com.cheack.softwareengineering.repository;

import com.cheack.softwareengineering.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, Long> {

    // 기존 것 – 다른 데서 쓰면 유지
    Page<Book> findByNameContainingIgnoreCaseOrAuthorContainingIgnoreCase(
            String nameKeyword,
            String authorKeyword,
            Pageable pageable
    );

    Optional<Book> findByIsbn(String isbn);

    /**
     * 공백 무시 검색 (JPQL + function)
     */
    @Query("""
           select b
             from Book b
            where lower(function('replace', b.name, ' ', '')) 
                      like concat('%', :keyword, '%')
               or lower(function('replace', b.author, ' ', '')) 
                      like concat('%', :keyword, '%')
           """)
    Page<Book> searchIgnoreSpaces(@Param("keyword") String keyword, Pageable pageable);
}