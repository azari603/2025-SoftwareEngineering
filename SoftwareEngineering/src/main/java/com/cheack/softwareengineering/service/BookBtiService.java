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

    // Q1~Q4: 첫 번째 축 (E/T/I)
    // answers: 1 -> A, 2 -> B, 3 -> C
    private static final char[][] FIRST_AXIS_MAPPING = new char[][]{
            {'E', 'T', 'I'},   // Q1
            {'E', 'T', 'I'},   // Q2
            {'E', 'T', 'I'},   // Q3
            {'E', 'I', 'T'}    // Q4 (A→E, B→I, C→T)
    };

    // Q5~Q8: 두 번째 축 (R/S/W)
    private static final char[][] SECOND_AXIS_MAPPING = new char[][]{
            {'R', 'S', 'W'},   // Q5
            {'R', 'S', 'W'},   // Q6
            {'R', 'S', 'W'},   // Q7
            {'R', 'S', 'W'}    // Q8
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
        if (answers == null || answers.size() != 8) {
            throw new IllegalArgumentException("answers는 8개의 선택(1~3)을 포함해야 합니다.");
        }

        Map<Character, Integer> firstAxisCount = new HashMap<>();
        Map<Character, Integer> secondAxisCount = new HashMap<>();

        // Q1~Q4 (0~3)
        for (int i = 0; i < 4; i++) {
            int choice = answers.get(i);
            char letter = mapChoice(FIRST_AXIS_MAPPING[i], choice);
            firstAxisCount.merge(letter, 1, Integer::sum);
        }

        // Q5~Q8 (4~7)
        for (int i = 4; i < 8; i++) {
            int choice = answers.get(i);
            char letter = mapChoice(SECOND_AXIS_MAPPING[i - 4], choice);
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
    public void saveResult(Long userId, BtiResultDto resultDto, List<Integer> rawAnswers) {
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

        btiResultRepository.save(entity);
    }

    /**
     * 저장된 BBTI 결과 조회
     */
    public BtiResultDto getResult(Long userId) {
        BookBTI entity = btiResultRepository.findByUserId(userId)
                .orElseThrow(() -> new NoSuchElementException("저장된 BBTI 결과가 없습니다. userId=" + userId));

        return BtiResultDto.fromEntity(entity);
    }

    /**
     * BBTI 결과를 바탕으로 책 추천.
     * 지금은 간단히 RecommendationService 를 통해 추천을 가져오도록 구성.
     * (나중에 코드별 큐레이션 로직을 추가할 수 있음)
     */
    public Page<BookCardDto> recommendFromResult(Long userId, Pageable pageable) {
        // 결과가 없으면 일단 인기 도서 폴백
        Optional<BookBTI> resultOpt = btiResultRepository.findByUserId(userId);
        if (resultOpt.isEmpty()) {
            return recommendationService.fallbackPopular(pageable);
        }

        // 일단은 BBTI와 무관하게 개인화 추천을 사용.
        // 필요하다면 resultType(code)에 따라 다른 전략을 넣을 수 있음.
        BookBTI result = resultOpt.get();
        BtiType type = BtiType.fromCode(result.getResultType());
        // type 정보를 이용한 커스텀 로직을 여기에 덧붙일 수 있음.

        return recommendationService.recommendForUser(userId, pageable);
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
