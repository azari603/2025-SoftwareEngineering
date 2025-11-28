package com.cheack.softwareengineering.service;

import com.cheack.softwareengineering.dto.BookReadingStatusResponse;
import com.cheack.softwareengineering.dto.ReadingStatusDto;
import com.cheack.softwareengineering.dto.ReadingStatusSummary;
import com.cheack.softwareengineering.entity.Book;
import com.cheack.softwareengineering.entity.ReadingStatus;
import com.cheack.softwareengineering.entity.ReadingStatusType;
import com.cheack.softwareengineering.entity.User;
import com.cheack.softwareengineering.repository.BookRepository;
import com.cheack.softwareengineering.repository.ReadingStatusRepository;
import com.cheack.softwareengineering.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReadingStatusService {

    private final ReadingStatusRepository readingStatusRepository;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;

    /**
     * setStatus(userId : Long, bookId : Long, status : ReadingStatusType) : void
     * - 유저/책 존재 여부 검증
     * - 기존 레코드 있으면 status만 변경
     * - 없으면 새로 생성
     */
    @Transactional
    public void setStatus(Long userId, Long bookId, ReadingStatusType status) {
        // 유저/책 존재 확인
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User not found: " + userId));

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new NoSuchElementException("Book not found: " + bookId));

        // 고유 제약: (user_id, book_id) 하나만 존재
        ReadingStatus readingStatus = readingStatusRepository
                .findByUserIdAndBookId(user.getId(), book.getId())
                .orElseGet(() -> ReadingStatus.builder()
                        .userId(user.getId())
                        .bookId(book.getId())
                        .build());

        readingStatus.setStatus(status);
        readingStatusRepository.save(readingStatus);
    }

    /**
     * clearStatus(userId : Long, bookId : Long) : void
     * - (user, book)에 대한 읽기 상태를 제거
     */
    @Transactional
    public void clearStatus(Long userId, Long bookId) {
        // 유저/책이 존재하지 않아도, 해당 상태만 삭제하면 되므로
        readingStatusRepository.deleteByUserIdAndBookId(userId, bookId);
    }

    /**
     * getByStatus(userId : Long, status : ReadingStatusType, p : Pageable) : Page<ReadingStatusDto>
     * - 특정 유저의 특정 status인 책 목록을 페이지로 조회
     */
    public Page<ReadingStatusDto> getByStatus(Long userId, ReadingStatusType status, Pageable pageable) {
        // 유저 존재 확인 (옵션이지만, 논리적으로는 있는 게 맞음)
        userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User not found: " + userId));

        Page<ReadingStatus> page = readingStatusRepository
                .findByUserIdAndStatus(userId, status, pageable);

        // 책 정보 한번에 로딩해서 DTO로 매핑
        List<Long> bookIds = page.getContent().stream()
                .map(ReadingStatus::getBookId)
                .distinct()
                .toList();

        Map<Long, Book> booksById = bookRepository.findAllById(bookIds).stream()
                .collect(Collectors.toMap(Book::getId, b -> b));

        return page.map(rs -> {
            Book book = booksById.get(rs.getBookId());
            return ReadingStatusDto.from(rs, book);
        });
    }

    /**
     * getSummary(userId : Long) : ReadingStatusSummary
     * - 상태별 개수를 집계해서 요약 정보 반환
     */
    public ReadingStatusSummary getSummary(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User not found: " + userId));

        Map<ReadingStatusType, Long> counts = new EnumMap<>(ReadingStatusType.class);
        for (ReadingStatusType type : ReadingStatusType.values()) {
            long count = readingStatusRepository.countByUserIdAndStatus(userId, type);
            counts.put(type, count);
        }
        return ReadingStatusSummary.of(counts);
    }

    /**
     * getStatusForBook(username, bookId) : BookReadingStatusResponse
     * - 로그인 사용자 기준, 특정 책에 대한 읽기 상태를 조회
     * - 상태가 없으면 hasStatus=false 로 반환
     */
    public BookReadingStatusResponse getStatusForBook(String username, Long bookId) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new NoSuchElementException("User not found: username=" + username));

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new NoSuchElementException("Book not found: " + bookId));

        return readingStatusRepository.findByUserIdAndBookId(user.getId(), book.getId())
                .map(BookReadingStatusResponse::from)
                .orElseGet(() -> BookReadingStatusResponse.empty(bookId));
    }
}