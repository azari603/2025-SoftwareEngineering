// src/main/java/com/cheack/softwareengineering/service/BtiAiRecommendationService.java
package com.cheack.softwareengineering.service;

import com.cheack.softwareengineering.dto.BtiResultDto;
import com.cheack.softwareengineering.dto.BookCardDto;
import com.cheack.softwareengineering.entity.Book;
import com.cheack.softwareengineering.repository.BookRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.openai.client.OpenAIClient;
import com.openai.models.ChatModel;
import com.openai.models.responses.Response;
import com.openai.models.responses.ResponseCreateParams;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BtiAiRecommendationService {

    private static final int CANDIDATE_SIZE = 50;   // AI에게 보여줄 후보 책 수
    private static final int RECOMMEND_SIZE = 10;   // 최종 추천 개수

    private final OpenAIClient openAIClient;
    private final BookRepository bookRepository;
    private final ObjectMapper objectMapper;

    /**
     * BBTI 결과를 기반으로 AI에게 추천을 맡긴 뒤,
     * book_id 목록을 받아서 실제 Book 엔티티 → BookCardDto 로 매핑.
     *
     * 실패하면 그냥 기본 인기 도서(books 테이블 페이징)로 폴백.
     */
    public Page<BookCardDto> recommendByBti(BtiResultDto bti, Pageable pageable) {
        try {
            // 1) 후보 책 목록 50권 정도 뽑기 (id 기준 최신순)
            Page<Book> candidatesPage = bookRepository.findAll(
                    PageRequest.of(0, CANDIDATE_SIZE, Sort.by(Sort.Direction.DESC, "id"))
            );
            List<Book> candidates = candidatesPage.getContent();

            if (candidates.isEmpty()) {
                return new PageImpl<>(List.of(), pageable, 0);
            }

            // 2) 프롬프트 생성
            String prompt = buildPrompt(bti, candidates);

            // 3) Responses API 호출
            ResponseCreateParams params = ResponseCreateParams.builder()
                    .model(ChatModel.GPT_4_1_MINI)
                    .input(prompt)
                    .maxOutputTokens(256L)
                    .build();

            Response response = openAIClient
                    .responses()
                    .create(params);

            // 4) 일단 output 전체를 문자열로 뽑는다 (형식: SDK 내부 구조 → toString())
            String rawOutput = String.valueOf(response.output());

            // 5) 그 안에서 [ ... ] 형태의 JSON 배열 부분만 잘라서 파싱 시도
            List<Long> selectedIds = parseBookIds(rawOutput);

            if (selectedIds.isEmpty()) {
                System.out.println("[BBTI-AI] 응답이 비어있거나 파싱 실패 → 후보 그대로 반환");
                return candidatesPage.map(BookCardDto::from);
            }

            // 6) 실제 Book 엔티티 조회 후, selectedIds 순서를 유지해서 DTO 리스트 만들기
            List<Book> selectedBooks = bookRepository.findAllById(selectedIds);
            Map<Long, Book> bookMap = selectedBooks.stream()
                    .collect(Collectors.toMap(Book::getId, b -> b));

            List<BookCardDto> orderedDtos = new ArrayList<>();
            for (Long id : selectedIds) {
                Book book = bookMap.get(id);
                if (book != null) {
                    orderedDtos.add(BookCardDto.from(book));
                }
            }

            // 7) pageable 에 맞게 자르기
            int total = orderedDtos.size();
            int offset = (int) pageable.getOffset();
            int endIndex = Math.min(offset + pageable.getPageSize(), total);

            List<BookCardDto> pageContent =
                    offset >= total ? List.of() : orderedDtos.subList(offset, endIndex);

            return new PageImpl<>(pageContent, pageable, total);

        } catch (Exception e) {
            System.out.println("[BBTI-AI] 예외 발생 → 기본 인기 도서로 폴백");
            e.printStackTrace();

            Page<Book> fallback = bookRepository.findAll(pageable);
            return fallback.map(BookCardDto::from);
        }
    }

    /**
     * BBTI 결과 + 후보 책 목록으로 프롬프트 생성
     * (카테고리 필드는 나중에 BtiResultDto에 추가되면 붙이면 됨)
     */
    private String buildPrompt(BtiResultDto bti, List<Book> candidates) {
        StringBuilder sb = new StringBuilder();

        sb.append("당신은 한국어 책 추천 전문가입니다.\n");
        sb.append("아래 사용자의 독서 성향에 맞는 책을 추천해주세요.\n");
        sb.append("반드시 아래 후보 목록에서만 선택하고, book_id 숫자만 JSON 배열로 출력하세요.\n");
        sb.append("예시: [1, 5, 10, 22]\n\n");

        sb.append("사용자 BBTI 정보:\n");
        sb.append("- code: ").append(bti.getCode()).append("\n");
        sb.append("- label: ").append(bti.getLabel()).append("\n");
        sb.append("- description: ").append(bti.getDescription()).append("\n\n");
        // BtiResultDto에 categories 추가되면 여기서 사용:
        // sb.append("- categories: ").append(String.join(", ", bti.getCategories())).append("\n\n");

        sb.append("후보 책 목록:\n");
        for (Book b : candidates) {
            sb.append(String.format(
                    "- book_id: %d, title: %s, author: %s, intro: %s%n",
                    b.getId(),
                    safe(b.getName()),
                    safe(b.getAuthor()),
                    truncate(safe(b.getIntro()), 160)
            ));
        }

        sb.append("\n요청: 위 후보 중에서 사용자에게 가장 잘 맞을 것 같은 책을 ")
                .append(RECOMMEND_SIZE)
                .append("권 선택하세요.\n")
                .append("출력은 오직 JSON 배열 형태의 book_id 숫자만 반환하세요. 예: [3, 10, 22]\n");

        return sb.toString();
    }

    private String safe(String s) {
        return s == null ? "" : s.replace("\n", " ");
    }

    private String truncate(String s, int maxLen) {
        if (s == null) return "";
        return s.length() <= maxLen ? s : s.substring(0, maxLen) + "...";
    }

    /**
     * AI 응답 문자열에서 [ ... ] 구간만 잘라서 JSON 배열로 파싱
     * → [1, 3, 5] 형태만 제대로 들어오면 잘 동작
     */
    private List<Long> parseBookIds(String text) {
        try {
            int start = text.indexOf('[');
            int end = text.lastIndexOf(']');

            if (start < 0 || end <= start) {
                return List.of();
            }

            String jsonArray = text.substring(start, end + 1);

            JsonNode node = objectMapper.readTree(jsonArray);
            List<Long> ids = new ArrayList<>();
            if (node.isArray()) {
                for (JsonNode n : node) {
                    if (n.isIntegralNumber()) {
                        ids.add(n.asLong());
                    }
                }
            }

            if (ids.size() > RECOMMEND_SIZE) {
                return ids.subList(0, RECOMMEND_SIZE);
            }
            return ids;
        } catch (Exception e) {
            System.out.println("[BBTI-AI] JSON 파싱 실패, raw text = " + text);
            e.printStackTrace();
            return List.of();
        }
    }
}
