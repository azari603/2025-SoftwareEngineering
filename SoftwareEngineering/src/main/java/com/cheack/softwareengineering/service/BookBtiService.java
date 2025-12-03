// src/main/java/com/cheack/softwareengineering/service/BookBtiService.java
package com.cheack.softwareengineering.service;

import com.cheack.softwareengineering.bti.BtiType;
import com.cheack.softwareengineering.dto.BtiQuestionDto;
import com.cheack.softwareengineering.dto.BtiResultDto;
import com.cheack.softwareengineering.dto.BookCardDto;
import com.cheack.softwareengineering.entity.BookBTI;
import com.cheack.softwareengineering.repository.BtiQuestionRepository;
import com.cheack.softwareengineering.repository.BtiResultRepository;
import com.cheack.softwareengineering.repository.BookRepository;
import com.cheack.softwareengineering.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookBtiService {

    private final BtiQuestionRepository btiQuestionRepository;
    private final BtiResultRepository btiResultRepository;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;   // 일단 향후 확장용
    private final RecommendationService recommendationService;
    private final BtiAiRecommendationService btiAiRecommendationService;

    // Q1~Q4: 첫 번째 축 (E/T/I)
    // answers: 1 -> A, 2 -> B, 3 -> C
    private static final char[][] FIRST_AXIS_MAPPING = new char[][]{
            {'E', 'T', 'I'},   // Q1
            {'E', 'T', 'I'},   // Q2
            {'E', 'T', 'I'},   // Q3
            {'E', 'T', 'I'},   // Q4 (A→E, B→I, C→T)
            {'E', 'T', 'I'},   // Q5
            {'E', 'T', 'I'},   // Q6
            {'E', 'T', 'I'},   // Q7
            {'E', 'T', 'I'},   // Q8
            {'E', 'T', 'I'},   // Q9
            {'E', 'T', 'I'}    // Q10
    };

    // Q5~Q8: 두 번째 축 (R/S/W)
    private static final char[][] SECOND_AXIS_MAPPING = new char[][]{
            {'R', 'S', 'W'},   // Q11
            {'R', 'S', 'W'},   // Q12
            {'R', 'S', 'W'},   // Q13
            {'R', 'S', 'W'},   // Q14
            {'R', 'S', 'W'},   // Q15
            {'R', 'S', 'W'},   // Q16
            {'R', 'S', 'W'},   // Q17
            {'R', 'S', 'W'},   // Q18
            {'R', 'S', 'W'},   // Q19
            {'R', 'S', 'W'}    // Q20

    };

    /**
     * BBTI 질문 리스트 조회
     */
    public List<BtiQuestionDto> getQuestions() {
        return btiQuestionRepository.findAll()
                .stream()
                .map(BtiQuestionDto::from)
                .collect(Collectors.toList());
    }

    /**
     * 사용자의 선택(1~3) 리스트로부터 ER/ES/... 코드 계산
     *
     * @param answers 크기 8, 각 원소 1~3 (A/B/C)
     */
    public BtiResultDto calculateResult(List<Integer> answers) {
        if (answers == null || answers.size() != 20) {
            throw new IllegalArgumentException("answers는 8개의 선택(1~3)을 포함해야 합니다.");
        }

        Map<Character, Integer> firstAxisCount = new HashMap<>();
        Map<Character, Integer> secondAxisCount = new HashMap<>();

        // Q1~Q4 (0~3)
        for (int i = 0; i < 10; i++) {
            int choice = answers.get(i);
            char letter = mapChoice(FIRST_AXIS_MAPPING[i], choice);
            firstAxisCount.merge(letter, 1, Integer::sum);
        }

        // Q5~Q8 (4~7)
        for (int i = 10; i < 20; i++) {
            int choice = answers.get(i);
            char letter = mapChoice(SECOND_AXIS_MAPPING[i - 10], choice);
            secondAxisCount.merge(letter, 1, Integer::sum);
        }

        char firstChar = pickDominant(firstAxisCount, new char[]{'E', 'T', 'I'});
        char secondChar = pickDominant(secondAxisCount, new char[]{'R', 'S', 'W'});

        String code = new String(new char[]{firstChar, secondChar});

        return BtiResultDto.fromCode(code);
    }

    /**
     * BBTI 결과 저장 (사용자당 1개 유지)
     */
    @Transactional
    public Long saveResult(Long userId, BtiResultDto resultDto, List<Integer> rawAnswers) {
        // user 존재 체크 (optional)
        userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다. id=" + userId));

        String code = resultDto.getCode();
        String answersStr = (rawAnswers != null ? rawAnswers.toString() : "");

        BookBTI entity = btiResultRepository.findByUserId(userId)
                .orElse(BookBTI.builder()
                        .userId(userId)
                        .build());

        // book_bti 컬럼 맞춰서 세팅
        entity.setQuestion("BOOK_BBTI_V1");  // 의미만 맞춰주는 상수
        entity.setAnswer(answersStr);
        entity.setResultType(code);
        // quizResult는 지금은 안 쓰므로 null 가능

        BookBTI saved = btiResultRepository.save(entity);
        return saved.getId();   // 🔹 resultId 반환
    }

    /**
     * 저장된 BBTI 결과 조회
     */
    public BtiResultDto getResult(Long userId) {
        BookBTI entity = btiResultRepository.findByUserId(userId)
                .orElseThrow(() -> new NoSuchElementException("저장된 BBTI 결과가 없습니다. userId=" + userId));

        return BtiResultDto.fromEntity(entity);
    }

    public BtiResultDto getResultById(Long resultId) {
        BookBTI entity = btiResultRepository.findById(resultId)
                .orElseThrow(() -> new NoSuchElementException("저장된 BBTI 결과가 없습니다. resultId=" + resultId));

        return BtiResultDto.fromEntity(entity);
    }

    /**
     * BBTI 결과를 바탕으로 책 추천.
     * 기존에는 RecommendationService.recommendForUser 를 사용했는데,
     * 이제는 BBTI 전용 AI 추천 서비스로 위임한다.
     */
    public Page<BookCardDto> recommendFromResult(Long userId, Pageable pageable) {
        Optional<BookBTI> resultOpt = btiResultRepository.findByUserId(userId);
        if (resultOpt.isEmpty()) {
            // 기존 로직 유지: BBTI가 없으면 인기 도서
            return recommendationService.fallbackPopular(pageable);
        }

        BookBTI result = resultOpt.get();
        BtiResultDto dto = BtiResultDto.fromEntity(result);

        // 🔹 여기서 AI 추천 사용
        return btiAiRecommendationService.recommendByBti(dto, pageable);
    }

    /**
     * resultId 기반 BBTI 결과를 바탕으로 책 추천.
     */
    public Page<BookCardDto> recommendFromResultId(Long resultId, Pageable pageable) {
        BookBTI entity = btiResultRepository.findById(resultId)
                .orElseThrow(() -> new NoSuchElementException("저장된 BBTI 결과가 없습니다. resultId=" + resultId));

        BtiResultDto resultDto = BtiResultDto.fromEntity(entity);
        return btiAiRecommendationService.recommendByBti(resultDto, pageable);
    }

    // === 내부 헬퍼 메서드들 ===

    private char mapChoice(char[] mapping, int choice) {
        int idx = choice - 1;
        if (idx < 0 || idx >= mapping.length) {
            throw new IllegalArgumentException("선택 값은 1~3 이어야 합니다. choice=" + choice);
        }
        return mapping[idx];
    }

    /**
     * 가장 많이 선택된 축 값을 반환 (동률이면 priority 순서대로)
     */
    private char pickDominant(Map<Character, Integer> countMap, char[] priorityOrder) {
        char best = priorityOrder[0];
        int bestCount = -1;

        for (char c : priorityOrder) {
            int cnt = countMap.getOrDefault(c, 0);
            if (cnt > bestCount) {
                best = c;
                bestCount = cnt;
            }
        }
        return best;
    }
}
