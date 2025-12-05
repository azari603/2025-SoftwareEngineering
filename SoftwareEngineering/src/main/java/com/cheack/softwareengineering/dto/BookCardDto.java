package com.cheack.softwareengineering.dto;

import com.cheack.softwareengineering.entity.Book;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 추천/목록 카드에서 쓸 간단한 도서 정보 DTO
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookCardDto {

    private Long bookId;
    private String name;
    private String author;
    private String imageUrl;

    // 추후에 통계 붙일 때 사용 가능 (평균 별점, 리뷰 수 등)
    private Double avgStar;
    private Long reviewCount;

    public static BookCardDto from(Book book) {
        if (book == null) {
            return null;
        }
        return BookCardDto.builder()
                .bookId(book.getId())
                .name(book.getName())
                .author(book.getAuthor())
                .imageUrl(book.getImage())
                .avgStar(null)      // TODO: 나중에 집계 테이블/쿼리 붙일 때 채우기
                .reviewCount(null)  // TODO: 위와 동일
                .build();
    }
}