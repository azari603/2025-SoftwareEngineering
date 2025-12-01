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

    /**
     * 특정 유저가 작성한 리뷰 중, 주어진 author 의 책 목록 (중복 제거, 페이징)
     */
    @Query("""
           select distinct b
             from Review r
             join Book b on r.bookId = b.id
            where r.userId = :userId
              and r.deleted = false
              and b.author = :author
           """)
    Page<Book> findBooksReviewedByUserAndAuthor(
            @Param("userId") Long userId,
            @Param("author") String author,
            Pageable pageable
    );
}