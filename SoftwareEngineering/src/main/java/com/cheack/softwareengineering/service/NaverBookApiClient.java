package com.cheack.softwareengineering.service;

import com.cheack.softwareengineering.dto.ExternalBookMeta;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 네이버 책 검색 API 호출 구현체.
 *
 * 주의: application.properties / application-dev.properties 등에
 *  naver.book.client-id, naver.book.client-secret
 *  값을 반드시 설정해야 한다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class NaverBookApiClient implements ExternalBookApiClient {

    private static final String BASE_URL = "https://openapi.naver.com";
    private static final String SEARCH_BOOK_PATH = "/v1/search/book.json";
    private static final DateTimeFormatter PUBDATE_FMT = DateTimeFormatter.BASIC_ISO_DATE;

    private final RestClient.Builder restClientBuilder;

    @Value("${naver.book.client-id}")
    private String clientId;

    @Value("${naver.book.client-secret}")
    private String clientSecret;

    private RestClient restClient() {
        return restClientBuilder
                .baseUrl(BASE_URL)
                .build();
    }

    @Override
    public ExternalBookMeta fetchByIsbn(String isbn) {
        try {
            RestClient client = restClient();

            NaverBookResponse response = client.get()
                    .uri(uriBuilder -> uriBuilder
                            .path(SEARCH_BOOK_PATH)
                            .queryParam("d_isbn", isbn)
                            .queryParam("display", 1)
                            .build())
                    .accept(MediaType.APPLICATION_JSON)
                    .header("X-Naver-Client-Id", clientId)
                    .header("X-Naver-Client-Secret", clientSecret)
                    .retrieve()
                    .body(NaverBookResponse.class);

            if (response == null || response.items == null || response.items.isEmpty()) {
                return null;
            }

            return toMeta(response.items.get(0));
        } catch (Exception e) {
            log.error("NaverBookApiClient.fetchByIsbn error: {}", e.getMessage(), e);
            return null;
        }
    }

    @Override
    public Page<ExternalBookMeta> search(String keyword, Pageable pageable) {
        if (keyword == null || keyword.isBlank()) {
            return Page.empty(pageable);
        }
        try {
            int size = pageable.getPageSize();
            int start = pageable.getPageNumber() * size + 1;

            RestClient client = restClient();

            NaverBookResponse response = client.get()
                    .uri(uriBuilder -> uriBuilder
                            .path(SEARCH_BOOK_PATH)
                            .queryParam("query", keyword)
                            .queryParam("display", size)
                            .queryParam("start", start)
                            .build())
                    .accept(MediaType.APPLICATION_JSON)
                    .header("X-Naver-Client-Id", clientId)
                    .header("X-Naver-Client-Secret", clientSecret)
                    .retrieve()
                    .body(NaverBookResponse.class);

            if (response == null || response.items == null || response.items.isEmpty()) {
                return new PageImpl<>(Collections.emptyList(), pageable, 0);
            }

            List<ExternalBookMeta> content = response.items.stream()
                    .map(this::toMeta)
                    .collect(Collectors.toList());

            long total = response.total != null ? response.total : content.size();

            return new PageImpl<>(content, pageable, total);
        } catch (Exception e) {
            log.error("NaverBookApiClient.search error: {}", e.getMessage(), e);
            return Page.empty(pageable);
        }
    }

    private ExternalBookMeta toMeta(NaverBookItem item) {
        ExternalBookMeta meta = new ExternalBookMeta();
        meta.setTitle(cleanHtml(item.title));
        meta.setImageUrl(item.image);
        meta.setAuthor(item.author);
        meta.setIntro(item.description);
        meta.setPublisher(item.publisher);
        meta.setIsbn(item.isbn);

        if (item.pubdate != null && item.pubdate.length() == 8) {
            try {
                LocalDate date = LocalDate.parse(item.pubdate, PUBDATE_FMT);
                meta.setPublicationDate(date);
            } catch (Exception e) {
                log.debug("Failed to parse pubdate: {}", item.pubdate);
            }
        }
        return meta;
    }

    private String cleanHtml(String text) {
        if (text == null) return null;
        // 네이버 응답의 <b>태그 등 제거
        return text.replaceAll("<.*?>", "");
    }

    /**
     * 네이버 book.json 응답용 내부 DTO
     */
    public static class NaverBookResponse {
        public Integer total;
        public Integer start;
        public Integer display;
        public List<NaverBookItem> items;
    }

    public static class NaverBookItem {
        public String title;
        public String link;
        public String image;
        public String author;
        public String publisher;
        public String isbn;
        public String description;
        public String pubdate;
    }
}
