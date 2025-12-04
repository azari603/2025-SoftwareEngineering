// src/main/java/com/cheack/softwareengineering/service/BtiAiRecommendationService.java
package com.cheack.softwareengineering.service;

import com.cheack.softwareengineering.dto.BtiResultDto;
import com.cheack.softwareengineering.dto.BookCardDto;
import com.cheack.softwareengineering.dto.BookSummaryDto;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.openai.client.OpenAIClient;
import com.openai.models.ChatModel;
import com.openai.models.responses.Response;
import com.openai.models.responses.ResponseCreateParams;
import com.openai.models.responses.ResponseOutputText;
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

    private static final int RECOMMEND_SIZE = 10;   // 최종 추천 개수

    private final OpenAIClient openAIClient;
    private final BookService bookService;           // ✅ 검색 로직 사용
    private final BookIngestService bookIngestService; // ✅ 외부 인입까지 포함
    private final ObjectMapper objectMapper;

    /**
     * BBTI 결과 기반 AI 추천
     * - AI에게는 BBTI 정보만 넘김
     * - AI가 돌려준 title/author 로 "기존 검색 시스템"을 그대로 사용해 책을 찾음
     * - 실패하면 기본 인기 도서로 폴백
     */
    @Transactional(readOnly = false)
    public Page<BookCardDto> recommendByBti(BtiResultDto bti, Pageable pageable) {
        try {
            // 1) 프롬프트 생성 (DB 책 목록 X)
            String prompt = buildPrompt(bti);

            // 2) Responses API 호출
            ResponseCreateParams params = ResponseCreateParams.builder()
                    .model(ChatModel.GPT_4_1_MINI)
                    .input(prompt)
                    .maxOutputTokens(512L)
                    .build();

            Response response = openAIClient.responses().create(params);

            // 3) output 에서 모델이 생성한 텍스트(JSON 배열)만 추출
            String rawOutput = extractAiText(response);
            System.out.println("[BBTI-AI] rawOutput = " + rawOutput);

            // 4) JSON 배열에서 title / author 목록 파싱
            List<AiBookCandidate> candidates = parseAiBooks(rawOutput);

            if (candidates.isEmpty()) {
                System.out.println("[BBTI-AI] 응답이 비었거나 파싱 실패 → 기본 인기 도서로 폴백");
                return fallback(pageable);
            }

            // 5) 각 후보를 "검색 시스템"으로 찾아서 BookCardDto 리스트 구성
            List<BookCardDto> recommended = new ArrayList<>();
            Set<Long> usedIds = new HashSet<>();

            for (AiBookCandidate c : candidates) {
                BookCardDto card = findBestMatchingCard(c);
                if (card == null) continue;

                if (card.getBookId() != null && usedIds.add(card.getBookId())) {
                    recommended.add(card);
                }

                if (recommended.size() >= RECOMMEND_SIZE) {
                    break;
                }
            }

            if (recommended.isEmpty()) {
                System.out.println("[BBTI-AI] AI 추천과 일치하는 책이 검색 시스템에서 안 나옴 → 폴백");
                return fallback(pageable);
            }

            // 6) pageable에 맞게 잘라서 Page 로 반환
            int total = recommended.size();
            int offset = (int) pageable.getOffset();
            int endIndex = Math.min(offset + pageable.getPageSize(), total);

            List<BookCardDto> pageContent =
                    offset >= total ? List.of() : recommended.subList(offset, endIndex);

            return new PageImpl<>(pageContent, pageable, total);

        } catch (Exception e) {
            System.out.println("[BBTI-AI] 예외 발생 → 기본 인기 도서로 폴백");
            e.printStackTrace();
            return fallback(pageable);
        }
    }

    /** BBTI 정보만으로 프롬프트 생성 */
    private String buildPrompt(BtiResultDto bti) {
        StringBuilder sb = new StringBuilder();

        sb.append("당신은 한국 독자를 위한 책 추천 전문가입니다.\n");
        sb.append("아래 사용자의 독서 성향(Book BBTI)에 잘 맞는, 한국어로 읽을 수 있는 책 10권을 추천해주세요.\n");
        sb.append("반드시 JSON 배열 형식으로만 답하고, 각 원소는 title(책 제목, 한국어), author(저자명)을 포함해야 합니다.\n");
        sb.append("예시:\n");
        sb.append("[{\"title\": \"연금술사\", \"author\": \"파울루 코엘류\"}, ");
        sb.append("{\"title\": \"어린 왕자\", \"author\": \"앙투안 드 생텍쥐페리\"}]\n\n");

        sb.append("사용자 BBTI 정보:\n");
        sb.append("- code: ").append(bti.getCode()).append("\n");
        sb.append("- label: ").append(bti.getLabel()).append("\n");
        sb.append("- description: ").append(bti.getDescription()).append("\n");

        return sb.toString();
    }

    /** AI 응답에서 JSON 배열(text)만 뽑기 */
    private String extractAiText(Response response) {
        // output: List<ResponseOutputItem>
        return response.output().stream()
                // Optional<ResponseOutputMessage> -> Stream<ResponseOutputMessage>
                .flatMap(item -> item.message().stream())
                // ResponseOutputMessage -> List<Content>
                .flatMap(message -> message.content().stream())
                // Content -> Optional<ResponseOutputText>
                .flatMap(content -> content.outputText().stream())
                // ResponseOutputText -> String (실제 텍스트)
                .map(ResponseOutputText::text)
                // 여러 조각이 있으면 줄바꿈으로 이어 붙이기
                .collect(Collectors.joining("\n"));
    }

    /** AI가 준 JSON 배열에서 title/author 리스트 파싱 */
    private List<AiBookCandidate> parseAiBooks(String text) {
        try {
            int start = text.indexOf('[');
            int end = text.lastIndexOf(']');

            if (start < 0 || end <= start) {
                return List.of();
            }

            String jsonArray = text.substring(start, end + 1);

            JsonNode node = objectMapper.readTree(jsonArray);
            List<AiBookCandidate> result = new ArrayList<>();

            if (node.isArray()) {
                for (JsonNode n : node) {
                    JsonNode titleNode = n.get("title");
                    if (titleNode == null || titleNode.asText().isBlank()) continue;

                    String title = titleNode.asText();
                    String author = n.hasNonNull("author") ? n.get("author").asText() : null;

                    result.add(new AiBookCandidate(title, author));
                }
            }

            if (result.size() > RECOMMEND_SIZE) {
                return result.subList(0, RECOMMEND_SIZE);
            }
            return result;
        } catch (Exception e) {
            System.out.println("[BBTI-AI] JSON 파싱 실패, raw text = " + text);
            e.printStackTrace();
            return List.of();
        }
    }

    /**
     * AI가 준 (title, author)로
     * 👉 SearchController 와 동일한 검색 알고리즘을 사용해서 Book 하나 골라오기
     */
    private BookCardDto findBestMatchingCard(AiBookCandidate c) {
        if (c.title == null || c.title.isBlank()) {
            return null;
        }

        Pageable firstPage = PageRequest.of(0, 10); // 상위 10권만 보면 충분

        // 🔍 1) SearchController 와 동일한 로직으로 검색 + 인입
        Page<BookSummaryDto> page = searchWithIngest(c.title, firstPage);
        if (page.isEmpty()) {
            return null;
        }

        // 🔍 2) author 까지 참고해서 "가장 잘 맞는" 책 하나 고르기
        String targetTitle = normalize(c.title);
        String targetAuthor = c.author != null ? normalize(c.author) : null;

        BookSummaryDto best = null;
        int bestScore = Integer.MIN_VALUE;

        for (BookSummaryDto s : page.getContent()) {
            String sTitle = normalize(s.getName());      // ★ 이름/필드는 실제 DTO에 맞게 조정
            String sAuthor = normalize(s.getAuthor());   // ★

            int score = 0;

            // 제목 일치도
            if (sTitle.equals(targetTitle)) {
                score += 3;
            } else if (sTitle.contains(targetTitle) || targetTitle.contains(sTitle)) {
                score += 2;
            }

            // 저자 일치도
            if (targetAuthor != null && !targetAuthor.isBlank()) {
                if (sAuthor.equals(targetAuthor)) {
                    score += 3;
                } else if (sAuthor.contains(targetAuthor) || targetAuthor.contains(sAuthor)) {
                    score += 2;
                }
            }

            // 가장 점수 높은 놈 선택
            if (score > bestScore) {
                bestScore = score;
                best = s;
            }
        }

        if (best == null) {
            // 그래도 아무것도 없으면 첫 번째 결과라도 사용
            best = page.getContent().get(0);
        }

        return toCard(best);
    }

    /** SearchController.searchBooks 의 알고리즘을 서비스 레벨로 복사한 버전 */
    private Page<BookSummaryDto> searchWithIngest(String keyword, Pageable pageable) {
        // 1) 먼저 우리 DB에서 검색
        Page<BookSummaryDto> page = bookService.search(keyword, pageable);

        // 키워드가 없거나, 이미 결과가 있다면 그대로 반환
        if (keyword == null || keyword.isBlank() || !page.isEmpty()) {
            return page;
        }

        // 2) DB 결과가 비었고, 키워드가 있을 때만 외부 API 인입 시도
        int ingestedCount = bookIngestService.ingestByQuery(keyword, pageable.getPageNumber());

        // 외부에서도 못 찾으면 그냥 기존 결과(빈 페이지) 반환
        if (ingestedCount <= 0) {
            return page;
        }

        // 3) 인입 후 다시 DB에서 검색해서 반환
        return bookService.search(keyword, pageable);
    }

    /** 검색 결과 BookSummaryDto → 카드용 BookCardDto 변환 */
    private BookCardDto toCard(BookSummaryDto s) {
        if (s == null) return null;

        // ⚠️ BookSummaryDto 필드 이름은 실제 구현에 맞게 수정해야 함
        return BookCardDto.builder()
                .bookId(s.getId())         // or getId()
                .name(s.getName())             // 제목 필드
                .author(s.getAuthor())
                .imageUrl(s.getImage())     // 썸네일 필드
                .build();
    }

    /** 기본 폴백: 검색 시스템으로 전체 목록 조회 */
    private Page<BookCardDto> fallback(Pageable pageable) {
        Page<BookSummaryDto> fallback = bookService.search(null, pageable);
        return fallback.map(this::toCard);
    }

    /** AI 출력을 담는 내부용 DTO */
    private record AiBookCandidate(String title, String author) {}

    private String normalize(String s) {
        if (s == null) return "";
        // 공백/대소문자/간단한 특수문자 제거해서 유사도 비교용
        return s.replaceAll("\\s+", "")
                .replaceAll("[\"'.,·]", "")
                .toLowerCase(Locale.ROOT);
    }
}
