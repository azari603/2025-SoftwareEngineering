// src/main/java/com/cheack/softwareengineering/dto/BookDetailDto.java
package com.cheack.softwareengineering.dto;

import com.cheack.softwareengineering.entity.Book;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookDetailDto {

    private Long id;
    private String name;
    private String image;
    private String author;
    private String intro;
    private String publisher;
    private String isbn;
    private LocalDate publicationDate;
    private Double avgStar;
    private Long reviewCount;

    public static BookDetailDto from(Book book, Double avgStar, Long reviewCount) {
        if (book == null) {
            return null;
        }

        double safeAvgStar = (avgStar != null) ? avgStar : 0.0;
        long safeReviewCount = (reviewCount != null) ? reviewCount : 0L;

        return BookDetailDto.builder()
                .id(book.getId())
                .name(book.getName())
                .image(book.getImage())
                .author(book.getAuthor())
                .intro(book.getIntro())
                .publisher(book.getPublisher())
                .isbn(book.getIsbn())
                .publicationDate(book.getPublicationDate())
                .avgStar(safeAvgStar)
                .reviewCount(safeReviewCount)
                .build();
    }

    public static BookDetailDto from(Book book) {
        return from(book, 0.0, 0L);
    }

}
