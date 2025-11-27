package com.cheack.softwareengineering.controller;

import com.cheack.softwareengineering.dto.BookSummaryDto;
import com.cheack.softwareengineering.service.BookIngestService;
import com.cheack.softwareengineering.service.BookService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

/**
 * 검색 전용 API
 *
 * Base: /api/v1/search
 *
 * - /api/v1/search/books
 *   제목/저자 키워드로 검색
 *   1) 먼저 DB 검색
 *   2) 결과가 비어 있으면 Naver API로 한 번 인입 후 다시 DB 검색
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/search")
@RequiredArgsConstructor
public class SearchController {

    private final BookService bookService;
    private final BookIngestService bookIngestService;

    /**
     * [도서 검색]
     * GET /api/v1/search/books?q=키워드&author=…&publisher=…&isbn=…&page&size&sort
     *
     * 지금은 q(키워드)만 사용해서 제목/저자 검색.
     * author/publisher/isbn 파라미터는 추후 확장용으로만 받는다.
     */
    @GetMapping("/books")
    public Page<BookSummaryDto> searchBooks(
            @RequestParam(name = "q", required = false) String keyword,
            @RequestParam(name = "author", required = false) String author,
            @RequestParam(name = "publisher", required = false) String publisher,
            @RequestParam(name = "isbn", required = false) String isbn,
            Pageable pageable
    ) {
        log.info("[SEARCH] 요청: keyword='{}', page={}, size={}",
                keyword, pageable.getPageNumber(), pageable.getPageSize());

        // 1) 먼저 우리 DB에서 검색
        Page<BookSummaryDto> page = bookService.search(keyword, pageable);

        // 현재 DB에서 찾은 결과 로그
        if (page.isEmpty()) {
            log.info("[SEARCH] DB 결과: 0건");
        } else {
            log.info("[SEARCH] DB 결과: {}건", page.getTotalElements());
            page.forEach(b ->
                    log.info("[SEARCH][DB-BOOK] {}", b) // BookSummaryDto에 toString 있으면 필드가 다 찍힘
            );
        }

        // 키워드가 없거나, 이미 결과가 있다면 그대로 반환
        if (keyword == null || keyword.isBlank() || !page.isEmpty()) {
            return page;
        }

        // 2) DB 결과가 비었고, 키워드가 있을 때만 외부 API 인입 시도
        int ingestedCount = bookIngestService.ingestByQuery(keyword, pageable.getPageNumber());
        log.info("[SEARCH] Naver 인입: keyword='{}', pageIndex={}, ingestedCount={}",
                keyword, pageable.getPageNumber(), ingestedCount);

        // 외부에서도 못 찾으면 그냥 빈 결과 반환
        if (ingestedCount <= 0) {
            log.info("[SEARCH] 외부 API에서도 신규 인입 없음 → 빈 페이지 반환");
            return page;
        }

        // 3) 인입 후 다시 DB에서 검색해서 반환
        Page<BookSummaryDto> afterPage = bookService.search(keyword, pageable);
        log.info("[SEARCH] 인입 후 DB 재검색 결과: {}건", afterPage.getTotalElements());
        afterPage.forEach(b ->
                log.info("[SEARCH][AFTER-INGEST-BOOK] {}", b)
        );

        return afterPage;
    }
}