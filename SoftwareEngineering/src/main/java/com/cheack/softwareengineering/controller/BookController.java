package com.cheack.softwareengineering.controller;

import com.cheack.softwareengineering.dto.BookDetailDto;
import com.cheack.softwareengineering.dto.BookDto;
import com.cheack.softwareengineering.dto.BookSummaryDto;
import com.cheack.softwareengineering.service.BookIngestService;
import com.cheack.softwareengineering.service.BookService;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Book Catalog / Meta API v1
 *
 * Base: /api/v1/books
 */
@RestController
@RequestMapping("/api/v1/books")
@RequiredArgsConstructor
public class BookController {

    private static final int MAX_BULK_SIZE = 50;

    private final BookService bookService;
    private final BookIngestService bookIngestService;

    /**
     * [도서 상세 조회]
     * GET /api/v1/books/{bookId}
     */
    @GetMapping("/{bookId}")
    public BookDetailDto getBook(@PathVariable Long bookId) {
        return bookService.getDetail(bookId);
    }

    /**
     * [ISBN 로 조회(존재 없으면 즉시 수집/등록)]
     * GET /api/v1/books/isbn/{isbn}
     *
     * 동작: DB 조회 → 없으면 외부 메타 호출(성공 시 저장 후 반환)
     * 외부에서도 못 찾으면 404
     */
    @GetMapping("/isbn/{isbn}")
    public BookDetailDto getByIsbn(@PathVariable String isbn) {
        // 1) DB 에 이미 있으면 그대로 반환
        return bookService.getByIsbn(isbn)
                .map(BookDetailDto::from)
                .orElseGet(() -> {
                    // 2) 없으면 외부 인입 시도
                    BookDetailDto ingested = bookService.ingestByIsbn(isbn);
                    if (ingested == null) {
                        throw new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Book not found for isbn=" + isbn
                        );
                    }
                    return ingested;
                });
    }

    /**
     * [도서 검색]
     * GET /api/v1/books?q=키워드&author=…&publisher=…&isbn=…&page&size&sort
     *
     * 지금은 q 만 BookService.search 로 넘기고,
     * author/publisher/isbn 필터는 추후 확장용으로만 받는다.
     */
    @GetMapping
    public Page<BookSummaryDto> searchBooks(
            @RequestParam(name = "q", required = false) String keyword,
            @RequestParam(name = "author", required = false) String author,
            @RequestParam(name = "publisher", required = false) String publisher,
            @RequestParam(name = "isbn", required = false) String isbn,
            Pageable pageable
    ) {
        // TODO: author/publisher/isbn 조건까지 포함하는 검색으로 확장
        return bookService.search(keyword, pageable);
    }

    /**
     * [여러 ISBN 일괄 조회(최대 N개)]
     * POST /api/v1/books/isbn/bulk
     *
     * 요청: { "isbns": ["...", "..."] }
     * 동작: 존재분은 그대로, 미존재는 외부 메타 호출 후 upsert
     * 응답: 성공 목록, 실패 목록(실패 사유 포함)
     */
    @PostMapping("/isbn/bulk")
    public BulkResultResponse ingestByIsbnBulk(@RequestBody BulkIsbnRequest request) {
        if (request == null || request.getIsbns() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "isbns must not be null");
        }

        // 공백/중복 제거 + 간단한 정리
        List<String> distinctIsbns = request.getIsbns().stream()
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(s -> !s.isBlank())
                .distinct()
                .collect(Collectors.toList());

        if (distinctIsbns.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "isbns must not be empty");
        }

        if (distinctIsbns.size() > MAX_BULK_SIZE) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Too many ISBNs; max allowed = " + MAX_BULK_SIZE
            );
        }

        List<BulkResultResponse.SuccessItem> successes = new ArrayList<>();
        List<BulkResultResponse.FailureItem> failures = new ArrayList<>();

        for (String isbn : distinctIsbns) {
            try {
                BookDto dto = bookIngestService.ingestByIsbn(isbn);
                if (dto == null) {
                    failures.add(
                            BulkResultResponse.FailureItem.builder()
                                    .isbn(isbn)
                                    .reason("BOOK_NOT_FOUND")
                                    .build()
                    );
                } else {
                    successes.add(
                            BulkResultResponse.SuccessItem.builder()
                                    .isbn(isbn)
                                    .bookId(dto.getId())
                                    .build()
                    );
                }
            } catch (Exception e) {
                failures.add(
                        BulkResultResponse.FailureItem.builder()
                                .isbn(isbn)
                                .reason(e.getMessage() != null ? e.getMessage() : "EXTERNAL_API_ERROR")
                                .build()
                );
            }
        }

        return BulkResultResponse.builder()
                .requestedCount(distinctIsbns.size())
                .successCount(successes.size())
                .failureCount(failures.size())
                .successes(successes)
                .failures(failures)
                .build();
    }

    // ===================== 요청/응답 DTO =====================

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BulkIsbnRequest {
        private List<String> isbns;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class BulkResultResponse {

        private int requestedCount;
        private int successCount;
        private int failureCount;
        private List<SuccessItem> successes;
        private List<FailureItem> failures;

        @Getter
        @NoArgsConstructor
        @AllArgsConstructor
        @Builder
        public static class SuccessItem {
            private String isbn;
            private Long bookId;
        }

        @Getter
        @NoArgsConstructor
        @AllArgsConstructor
        @Builder
        public static class FailureItem {
            private String isbn;
            private String reason;
        }
    }
}
