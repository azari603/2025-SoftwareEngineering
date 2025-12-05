package com.cheack.softwareengineering.dto;

import com.cheack.softwareengineering.entity.Book;
import com.cheack.softwareengineering.entity.ReadingStatus;
import com.cheack.softwareengineering.entity.ReadingStatusType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 읽기 상태 + 책 요약 정보 DTO
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReadingStatusDto {

    private Long readingStatusId;
    private Long userId;
    private Long bookId;

    private ReadingStatusType status;

    // 책 정보 요약
    private String bookName;
    private String bookAuthor;
    private String bookImage;

    public static ReadingStatusDto from(ReadingStatus rs, Book book) {
        return ReadingStatusDto.builder()
                .readingStatusId(rs.getId())
                .userId(rs.getUserId())
                .bookId(rs.getBookId())
                .status(rs.getStatus())
                .bookName(book != null ? book.getName() : null)
                .bookAuthor(book != null ? book.getAuthor() : null)
                .bookImage(book != null ? book.getImage() : null)
                .build();
    }
}
