// src/main/java/com/cheack/softwareengineering/service/BookIngestService.java
package com.cheack.softwareengineering.service;

import com.cheack.softwareengineering.dto.BookDto;
import com.cheack.softwareengineering.dto.ExternalBookMeta;
import com.cheack.softwareengineering.entity.Book;
import com.cheack.softwareengineering.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;

/**
 * 외부 도서 메타(네이버 등)를 우리 DB(Book)에 넣어주는 전용 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class BookIngestService {

    private final BookRepository bookRepository;
    private final ExternalBookApiClient externalBookApiClient;

    // 다이어그램에 있던 clock 필드 (일단 기본 시스템 클럭으로 사용)
    private final Clock clock = Clock.systemDefaultZone();

    /**
     * ISBN 으로 단일 도서를 보장 수준으로 DB에 채워 넣고 BookDto 로 반환
     * - 1) 이미 DB에 있으면 그대로 DTO 변환
     * - 2) 없으면 외부 API 조회 후 upsert 하고 DTO 반환
     * - 3) 외부에서도 못 찾으면 null 반환 (상위 서비스에서 처리)
     */
    public BookDto ingestByIsbn(String isbn) {
        return bookRepository.findByIsbn(isbn)
                .map(BookDto::fromEntity)
                .orElseGet(() -> fetchAndSaveByIsbn(isbn));
    }

    private BookDto fetchAndSaveByIsbn(String isbn) {
        ExternalBookMeta meta = externalBookApiClient.fetchByIsbn(isbn);
        if (meta == null) {
            log.info("External book meta not found for isbn={}", isbn);
            return null;
        }
        Book saved = upsert(meta);
        return BookDto.fromEntity(saved);
    }

    /**
     * 키워드 검색 결과 한 페이지를 외부에서 가져와서
     * 각 도서를 upsert 하고, DB에 실제로 반영한 건수를 반환.
     *
     * page 는 0-based 라고 가정.
     * size 는 일단 상수 20 사용 (나중에 요구사항에 맞게 수정 가능).
     */
    public int ingestByQuery(String query, int page) {
        int size = 20;
        Pageable pageable = PageRequest.of(page, size);

        Page<ExternalBookMeta> metaPage = externalBookApiClient.search(query, pageable);
        int count = 0;

        for (ExternalBookMeta meta : metaPage.getContent()) {
            upsert(meta);
            count++;
        }
        return count;
    }

    /**
     * 외부 메타를 받아서
     * - 이미 같은 ISBN 이 DB에 있으면 내용 업데이트
     * - 없으면 새 Book 엔티티 생성
     * 후 save 해서 반환.
     */
    public Book upsert(ExternalBookMeta meta) {
        validate(meta);

        return bookRepository.findByIsbn(meta.getIsbn())
                .map(existing -> {
                    applyMeta(existing, meta);
                    return bookRepository.save(existing);
                })
                .orElseGet(() -> bookRepository.save(toEntity(meta)));
    }

    /**
     * 외부 메타에 필수값이 제대로 들어있는지 검증
     * - title, isbn 은 필수
     * - author 는 없으면 "저자 미상" 으로 채운다
     */
    public void validate(ExternalBookMeta meta) {
        if (meta == null) {
            throw new IllegalArgumentException("ExternalBookMeta must not be null");
        }
        if (meta.getTitle() == null || meta.getTitle().isBlank()) {
            throw new IllegalArgumentException("Book title is required");
        }
        if (meta.getIsbn() == null || meta.getIsbn().isBlank()) {
            throw new IllegalArgumentException("Book ISBN is required");
        }

        // author 는 옵션: 없으면 기본값 "저자 미상"
        if (meta.getAuthor() == null || meta.getAuthor().isBlank()) {
            log.debug("External book meta has no author. isbn={}, title='{}' → set '저자 미상'",
                    meta.getIsbn(), meta.getTitle());
            meta.setAuthor("저자 미상");
        }
    }

    /**
     * 외부 메타를 새 Book 엔티티로 변환
     */
    public Book toEntity(ExternalBookMeta meta) {
        validate(meta);

        return Book.builder()
                .name(meta.getTitle())
                .image(meta.getImageUrl())
                .author(meta.getAuthor())
                .intro(meta.getIntro())
                .publisher(meta.getPublisher())
                .isbn(meta.getIsbn())
                .publicationDate(meta.getPublicationDate())
                .build();
    }

    /**
     * 존재하는 Book 엔티티에 외부 메타 내용을 덮어쓰는 헬퍼
     */
    private void applyMeta(Book book, ExternalBookMeta meta) {
        // validate 안에서 author 기본값까지 세팅되었다고 가정
        book.setName(meta.getTitle());
        book.setImage(meta.getImageUrl());
        book.setAuthor(meta.getAuthor());
        book.setIntro(meta.getIntro());
        book.setPublisher(meta.getPublisher());
        book.setIsbn(meta.getIsbn());
        book.setPublicationDate(meta.getPublicationDate());
    }
}
