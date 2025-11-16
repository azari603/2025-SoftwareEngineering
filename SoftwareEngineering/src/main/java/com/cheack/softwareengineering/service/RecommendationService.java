package com.cheack.softwareengineering.service;

import com.cheack.softwareengineering.dto.BookCardDto;
import com.cheack.softwareengineering.entity.Book;
import com.cheack.softwareengineering.recommendation.RecommendationEngine;
import com.cheack.softwareengineering.repository.BookRepository;
import com.cheack.softwareengineering.repository.ReadingStatusRepository;
import com.cheack.softwareengineering.repository.ReviewLikeRepository;
import com.cheack.softwareengineering.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 추천 API V1 의 서비스 계층.
 *
 * - /api/v1/recommendations/me
 * - /api/v1/books/{bookId}/similar
 * - /api/v1/recommendations/popular
 *
 * 컨트롤러에서 이 서비스를 호출해서 BookCardDto 기반으로 응답을 만들게 된다.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RecommendationService {

    private final ReadingStatusRepository readingStatusRepository;
    private final ReviewRepository reviewRepository;
    private final ReviewLikeRepository reviewLikeRepository;
    private final BookRepository bookRepository;
    private final RecommendationEngine recommendationEngine;

    /**
     * 개인화 추천: GET /recommendations/me
     *
     * 지금은 RecommendationEngine 에서 userId 기준 bookId 리스트만 던져준다고 가정하고,
     * 여기서 Book 을 조회해서 BookCardDto 로 매핑한다.
     */
    public Page<BookCardDto> recommendForUser(Long userId, Pageable pageable) {
        if (userId == null) {
            // 비로그인 사용자는 그냥 인기 도서로 폴백
            return fallbackPopular(pageable);
        }

        int pageSize = pageable.getPageSize();
        int offset = (int) pageable.getOffset();
        int needed = offset + pageSize;

        // 엔진에서 넉넉히 가져와서 우리가 페이지를 끊는다.
        List<Long> allRecommendedIds = recommendationEngine.recommendForUser(userId, needed);

        if (allRecommendedIds.isEmpty()) {
            // 엔진이 아직 비어있으면 인기 도서로 폴백
            return fallbackPopular(pageable);
        }

        List<Long> pageIds;
        if (offset >= allRecommendedIds.size()) {
            pageIds = Collections.emptyList();
        } else {
            pageIds = allRecommendedIds.subList(offset, Math.min(needed, allRecommendedIds.size()));
        }

        return toBookCardPage(pageIds, pageable, allRecommendedIds.size());
    }

    /**
     * 유사 도서: GET /books/{bookId}/similar
     */
    public Page<BookCardDto> similarBooks(Long bookId, Pageable pageable) {
        int pageSize = pageable.getPageSize();
        int offset = (int) pageable.getOffset();
        int needed = offset + pageSize;

        List<Long> allSimilarIds = recommendationEngine.similarItems(bookId, needed);

        if (allSimilarIds.isEmpty()) {
            // 일단은 popular 로 폴백. 필요하면 "같은 작가/출판사" 로직을 여기서 추가해도 됨.
            return fallbackPopular(pageable);
        }

        List<Long> pageIds;
        if (offset >= allSimilarIds.size()) {
            pageIds = Collections.emptyList();
        } else {
            pageIds = allSimilarIds.subList(offset, Math.min(needed, allSimilarIds.size()));
        }

        return toBookCardPage(pageIds, pageable, allSimilarIds.size());
    }

    /**
     * 인기 도서(폴백): GET /recommendations/popular
     *
     * 지금은 가장 단순하게 Book 테이블을 그대로 페이징해서 반환.
     * 나중에 Review/Like/ReadingStatus 집계를 써서 "진짜 인기" 로직으로 바꾸면 된다.
     */
    public Page<BookCardDto> fallbackPopular(Pageable pageable) {
        Page<Book> page = bookRepository.findAll(pageable);
        List<BookCardDto> dtos = page
                .getContent()
                .stream()
                .map(BookCardDto::from)
                .collect(Collectors.toList());

        return new PageImpl<>(dtos, pageable, page.getTotalElements());
    }

    /**
     * bookId 리스트 → BookCardDto Page 로 변환하는 공통 헬퍼
     */
    private Page<BookCardDto> toBookCardPage(List<Long> bookIds, Pageable pageable, int totalCount) {
        if (bookIds.isEmpty()) {
            return new PageImpl<>(Collections.emptyList(), pageable, totalCount);
        }

        // findAllById 는 순서를 보장하지 않으므로, map 으로 묶은 뒤 ID 순서대로 재정렬한다.
        List<Book> books = bookRepository.findAllById(bookIds);
        Map<Long, Book> bookMap = books.stream()
                .collect(Collectors.toMap(Book::getId, b -> b));

        List<BookCardDto> dtos = new ArrayList<>();
        for (Long id : bookIds) {
            Book book = bookMap.get(id);
            if (book != null) {
                dtos.add(BookCardDto.from(book));
            }
        }

        return new PageImpl<>(dtos, pageable, totalCount);
    }
}