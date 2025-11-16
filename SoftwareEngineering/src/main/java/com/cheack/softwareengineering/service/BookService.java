// src/main/java/com/cheack/softwareengineering/service/BookService.java
package com.cheack.softwareengineering.service;

import com.cheack.softwareengineering.dto.BookDetailDto;
import com.cheack.softwareengineering.dto.BookSummaryDto;
import com.cheack.softwareengineering.dto.BookUpsertCmd;
import com.cheack.softwareengineering.entity.Book;
import com.cheack.softwareengineering.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookService {

    private final BookRepository bookRepository;
    private final BookIngestService bookIngestService;

    /**
     * 키워드로 책 검색 (제목/저자에 대해 부분 일치, 대소문자 무시)
     * keyword 가 비어있으면 전체 목록을 페이징.
     */
    public Page<BookSummaryDto> search(String keyword, Pageable pageable) {
        Page<Book> page;
        if (keyword == null || keyword.isBlank()) {
            page = bookRepository.findAll(pageable);
        } else {
            page = bookRepository.findByNameContainingIgnoreCaseOrAuthorContainingIgnoreCase(
                    keyword, keyword, pageable
            );
        }
        return page.map(BookSummaryDto::from);
    }

    /**
     * bookId 로 상세 조회
     */
    public BookDetailDto getDetail(Long bookId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new IllegalArgumentException("Book not found: id=" + bookId));
        return BookDetailDto.from(book);
    }

    /**
     * ISBN 으로 Book 엔티티 조회
     */
    public Optional<Book> getByIsbn(String isbn) {
        return bookRepository.findByIsbn(isbn);
    }

    /**
     * 주어진 정보로 책을 "보장"해 주는 메서드.
     *
     * - isbn 이 이미 존재하면 해당 Book 을 업데이트
     * - 없으면 새 Book 생성
     * - 최종적으로 Book id 반환
     */
    @Transactional
    public Long ensureBook(BookUpsertCmd cmd) {
        if (cmd == null) {
            throw new IllegalArgumentException("BookUpsertCmd must not be null");
        }
        String isbn = cmd.getIsbn();

        if (isbn != null && !isbn.isBlank()) {
            Optional<Book> existingOpt = bookRepository.findByIsbn(isbn);
            if (existingOpt.isPresent()) {
                Book existing = existingOpt.get();
                applyCmd(existing, cmd);
                return bookRepository.save(existing).getId();
            }
        }

        Book book = new Book();
        applyCmd(book, cmd);
        return bookRepository.save(book).getId();
    }

    /**
     * 외부 API 를 통해 ISBN 으로 책을 인입하고,
     * 최종적으로 DB 에 있는 Book 기준으로 상세 DTO 반환.
     *
     * 외부/DB 에서도 못 찾으면 null 반환.
     */
    @Transactional
    public BookDetailDto ingestByIsbn(String isbn) {
        // 외부 인입 시도 (못 찾으면 null)
        var ingestResult = bookIngestService.ingestByIsbn(isbn);
        if (ingestResult == null) {
            return null;
        }

        // 인입 후 DB 에 있는 Book 을 기준으로 상세 조회
        Book book = bookRepository.findByIsbn(isbn)
                .orElseThrow(() -> new IllegalStateException("Book not found after ingest. isbn=" + isbn));
        return BookDetailDto.from(book);
    }

    /**
     * 여러 ISBN 을 한꺼번에 인입.
     * (에러가 나더라도 나머지는 계속 진행)
     */
    @Transactional
    public void ingestBatch(List<String> isbns) {
        if (isbns == null || isbns.isEmpty()) {
            return;
        }
        for (String isbn : isbns) {
            if (isbn == null || isbn.isBlank()) {
                continue;
            }
            try {
                bookIngestService.ingestByIsbn(isbn);
            } catch (Exception e) {
                // TODO: 필요하면 로깅/모니터링 추가
                // log.warn("Failed to ingest isbn={}", isbn, e);
            }
        }
    }

    // === 내부 헬퍼 ===

    private void applyCmd(Book book, BookUpsertCmd cmd) {
        book.setName(cmd.getName());
        book.setImage(cmd.getImage());
        book.setAuthor(cmd.getAuthor());
        book.setIntro(cmd.getIntro());
        book.setPublisher(cmd.getPublisher());
        book.setIsbn(cmd.getIsbn());
        book.setPublicationDate(cmd.getPublicationDate());
    }
}
