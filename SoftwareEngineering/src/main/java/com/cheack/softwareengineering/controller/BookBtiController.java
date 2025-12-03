// src/main/java/com/cheack/softwareengineering/controller/BookBtiController.java
package com.cheack.softwareengineering.controller;

import com.cheack.softwareengineering.dto.BtiQuestionDto;
import com.cheack.softwareengineering.dto.UserDto;
import com.cheack.softwareengineering.dto.BtiResultDto;
import com.cheack.softwareengineering.dto.BookCardDto;
import com.cheack.softwareengineering.service.BookBtiService;
import com.cheack.softwareengineering.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.Authentication;
import java.util.ArrayList;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * BookBTI API v1
 *
 * Base: /api/v1/bookbti
 * Auth: 로그인 권장(미로그인도 세션 방식으로 체험 가능)
 *
 * 설계서 기준 엔드포인트:
 *  - GET  /bookbti/questions?version=1
 *  - POST /bookbti/sessions                : 검사 세션 시작(start)
 *  - GET  /bookbti/sessions/{sessionId}/question
 *  - POST /bookbti/sessions/{sessionId}/answers
 *  - POST /bookbti/sessions/{sessionId}/undo
 *  - POST /bookbti/sessions/{sessionId}/finish
 *  - GET  /bookbti/sessions/{sessionId}/result
 *  - GET  /bookbti/sessions/{sessionId}/recommendations?page,size
 */
@RestController
@RequestMapping("/api/v1/bookbti")
@RequiredArgsConstructor
public class BookBtiController {

    private final BookBtiService bookBtiService;
    private final UserService userService;

    /**
     * 매우 단순한 인메모리 세션 저장소.
     * (실 서비스에서는 Redis 등으로 대체하는 것을 권장)
     */
    private final Map<String, SessionState> sessions = new ConcurrentHashMap<>();

    // ===================== 공통 메타 =====================

    /**
     * [질문 목록 조회]
     * GET /api/v1/bookbti/questions?version=1
     *
     * 현재는 version=1만 지원하고, 전체 문항을 한 번에 내려준다.
     */
    @GetMapping("/questions")
    public List<BtiQuestionDto> getQuestionsMeta(
            @RequestParam(name = "version", defaultValue = "1") int version
    ) {
        // version 파라미터는 향후 확장용
        return bookBtiService.getQuestions();
    }

    // ===================== 세션 플로우 =====================

    /**
     * [세션 생성(시작)]
     * POST /api/v1/bookbti/sessions
     *
     * 응답: sessionId
     */
    @PostMapping("/sessions")
    public CreateSessionResponse start(
            @AuthenticationPrincipal String username
    ) {

        if (username == null || "anonymousUser".equals(username)) {
            throw new IllegalArgumentException("UNAUTHORIZED");
        }

        UserDto user = userService.getByUsername(username);
        Long userId = user.getId();

        String sessionId = UUID.randomUUID().toString();
        SessionState session = new SessionState(sessionId, userId);
        sessions.put(sessionId, session);

        return new CreateSessionResponse(sessionId);
    }

    /**
     * [현재까지 진행 기준 다음 문항 조회]
     * GET /api/v1/bookbti/sessions/{sessionId}/question
     *
     * - 아직 답변하지 않은 "다음" 문항 하나를 내려준다.
     * - 20문항 모두 답변했다면 예외 발생(글로벌 핸들러에서 적절히 에러 포맷으로 래핑).
     */
    @GetMapping("/sessions/{sessionId}/question")
    public BtiQuestionDto getQuestion(
            @PathVariable String sessionId
    ) {
        SessionState session = getSessionOrThrow(sessionId);
        List<BtiQuestionDto> questions = bookBtiService.getQuestions();

        int idx = session.getAnswers().size(); // 다음 질문 인덱스
        if (idx >= questions.size()) {
            throw new IllegalStateException("이미 모든 문항에 답변했습니다.");
        }

        return questions.get(idx);
    }

    /**
     * [답변 제출]
     * POST /api/v1/bookbti/sessions/{sessionId}/answers
     *
     * 요청: { "choice": 1|2|3 }
     * 응답: 다음 문항(없으면 null)
     */
    @PostMapping("/sessions/{sessionId}/answers")
    public BtiQuestionDto answer(
            @PathVariable String sessionId,
            @RequestBody AnswerRequest request
    ) {
        SessionState session = getSessionOrThrow(sessionId);

        if (session.isFinished()) {
            throw new IllegalStateException("이미 종료된 세션입니다.");
        }

        int choice = request.getChoice();
        if (choice < 1 || choice > 3) {
            throw new IllegalArgumentException("choice는 1~3 사이여야 합니다.");
        }

        if (session.getAnswers().size() >= 20) {
            throw new IllegalStateException("이미 모든 문항에 답변했습니다.");
        }

        session.getAnswers().add(choice);

        // 다음 문항 반환 (없으면 null)
        List<BtiQuestionDto> questions = bookBtiService.getQuestions();
        int idx = session.getAnswers().size();
        if (idx >= questions.size()) {
            return null; // 더 이상 질문이 없으면 null
        }
        return questions.get(idx);
    }

    /**
     * [되돌리기]
     * POST /api/v1/bookbti/sessions/{sessionId}/undo
     *
     * 응답: 되돌리기 성공 여부와 현재 답변 개수(다음 문항 index 용)
     */
    @PostMapping("/sessions/{sessionId}/undo")
    public UndoResponse undo(@PathVariable String sessionId) {
        SessionState session = getSessionOrThrow(sessionId);

        if (session.getAnswers().isEmpty()) {
            return new UndoResponse(false, 0);
        }

        session.getAnswers().remove(session.getAnswers().size() - 1);
        return new UndoResponse(true, session.getAnswers().size());
    }

    /**
     * [종료/결과 산출]
     * POST /api/v1/bookbti/sessions/{sessionId}/finish
     *
     * - 20문항이 모두 채워졌을 때만 종료 가능.
     * - 로그인 사용자면 BookBtiService.saveResult 통해 결과 영속화.
     * - 응답: resultId(저장된 경우) + BtiResultDto
     */
    @PostMapping("/sessions/{sessionId}/finish")
    public BtiFinishResponse finish(
            @PathVariable String sessionId,
            Authentication authentication
    ) {
        SessionState session = getSessionOrThrow(sessionId);

        if (session.isFinished()) {
            // 이미 끝난 세션이면, 저장은 하지 않고 계산 결과만 다시 내려준다.
            BtiResultDto result = bookBtiService.calculateResult(session.getAnswers());
            return new BtiFinishResponse(null, result);
        }

        if (session.getAnswers().size() != 20) {
            throw new IllegalStateException("모든 문항(20개)에 답변한 후 종료할 수 있습니다.");
        }

        BtiResultDto result = bookBtiService.calculateResult(session.getAnswers());

        Long ownerUserId = session.getUserId();

        // 세션 시작 시점에 userId가 없고, 지금은 로그인한 상태라면 userId 보정
        if (ownerUserId == null && authentication != null
                && authentication.isAuthenticated()
                && !"anonymousUser".equals(authentication.getPrincipal())) {

            String username = authentication.getName();   // JWT의 subject = username
            UserDto user = userService.getByUsername(username);
            ownerUserId = user.getId();
        }

        Long resultId = null;
        if (ownerUserId != null) {
            resultId = bookBtiService.saveResult(
                    ownerUserId,
                    result,
                    new ArrayList<>(session.getAnswers())
            );
        }

        session.setFinished(true);
        return new BtiFinishResponse(resultId, result);
    }

    /**
     * [세션 기준 결과 조회]
     * GET /api/v1/bookbti/sessions/{sessionId}/result
     *
     * (기존 기능 – 필요하면 계속 사용)
     */
    @GetMapping("/sessions/{sessionId}/result")
    public BtiResultDto result(@PathVariable String sessionId) {
        SessionState session = getSessionOrThrow(sessionId);

        if (session.getAnswers().size() != 20) {
            throw new IllegalStateException("아직 모든 문항에 답변하지 않았습니다.");
        }

        return bookBtiService.calculateResult(session.getAnswers());
    }


    /**
     * [결과 기반 추천 - 세션 버전]
     * GET /api/v1/bookbti/sessions/{sessionId}/recommendations?page,size
     *
     * (기존 기능 – 필요하면 유지)
     */
    @GetMapping("/sessions/{sessionId}/recommendations")
    public Page<BookCardDto> recommendFromResult(
            @PathVariable String sessionId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication authentication   // 🔁 변경
    ) {
        Pageable pageable = PageRequest.of(page, size);

        Long targetUserId = null;

        // 1) JWT 로 인증된 경우: SecurityContext 에서 username 꺼내기
        if (authentication != null
                && authentication.isAuthenticated()
                && !"anonymousUser".equals(authentication.getPrincipal())) {

            String username = authentication.getName();   // JwtAuthenticationFilter 에서 넣어준 username
            UserDto me = userService.getByUsername(username);
            targetUserId = me.getId();
        }

        // 2) 그래도 null이면 세션에 저장된 userId 사용 (세션 시작 당시 로그인 유저)
        if (targetUserId == null) {
            SessionState session = getSessionOrThrow(sessionId);
            targetUserId = session.getUserId();  // 비로그인 세션이면 null → service 에서 fallbackPopular
        }

        return bookBtiService.recommendFromResult(targetUserId, pageable);
    }



    /**
     * [결과 조회 - resultId 기반]
     * GET /api/v1/bookbti/results/{resultId}
     */
    @GetMapping("/results/{resultId}")
    public BtiResultDto getResultById(@PathVariable Long resultId) {
        return bookBtiService.getResultById(resultId);
    }

    /**
     * [결과 기반 추천 - resultId 기반]
     * GET /api/v1/bookbti/results/{resultId}/recommendations?page,size
     */
    @GetMapping("/results/{resultId}/recommendations")
    public Page<BookCardDto> recommendFromResultId(
            @PathVariable Long resultId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return bookBtiService.recommendFromResultId(resultId, pageable);
    }



    // ===================== 내부 유틸/DTO =====================

    private SessionState getSessionOrThrow(String sessionId) {
        SessionState session = sessions.get(sessionId);
        if (session == null) {
            throw new NoSuchElementException("세션을 찾을 수 없습니다. id=" + sessionId);
        }
        return session;
    }



    /**
     * 세션 상태(인메모리)
     */
    private static class SessionState {
        private final String sessionId;
        private final Long userId;          // 시작 시점의 사용자 (비로그인은 null)
        private final List<Integer> answers = new ArrayList<>();
        private boolean finished;

        private SessionState(String sessionId, Long userId) {
            this.sessionId = sessionId;
            this.userId = userId;
        }

        public Long getUserId() {
            return userId;
        }

        public List<Integer> getAnswers() {
            return answers;
        }

        public boolean isFinished() {
            return finished;
        }

        public void setFinished(boolean finished) {
            this.finished = finished;
        }
    }

    /**
     * 세션 생성 응답 DTO
     */
    public static class CreateSessionResponse {
        private final String sessionId;

        public CreateSessionResponse(String sessionId) {
            this.sessionId = sessionId;
        }

        public String getSessionId() {
            return sessionId;
        }
    }

    /**
     * 답변 요청 DTO
     * choice: 1(A) | 2(B) | 3(C)
     */
    public static class AnswerRequest {
        private int choice;

        public AnswerRequest() {
        }

        public AnswerRequest(int choice) {
            this.choice = choice;
        }

        public int getChoice() {
            return choice;
        }

        public void setChoice(int choice) {
            this.choice = choice;
        }
    }

    /**
     * 되돌리기 응답 DTO
     * - success: 되돌리기 성공 여부
     * - currentIndex: 현재까지 답변된 문항 개수(다음 질문 인덱스)
     */
    public static class UndoResponse {
        private final boolean success;
        private final int currentIndex;

        public UndoResponse(boolean success, int currentIndex) {
            this.success = success;
            this.currentIndex = currentIndex;
        }

        public boolean isSuccess() {
            return success;
        }

        public int getCurrentIndex() {
            return currentIndex;
        }
    }

    /**
     * finish 응답 DTO
     * - 저장된 경우: resultId != null
     * - 비로그인/미저장: resultId == null
     */
    public static class BtiFinishResponse {
        private final Long resultId;
        private final BtiResultDto result;

        public BtiFinishResponse(Long resultId, BtiResultDto result) {
            this.resultId = resultId;
            this.result = result;
        }

        public Long getResultId() {
            return resultId;
        }

        public BtiResultDto getResult() {
            return result;
        }
    }
}
