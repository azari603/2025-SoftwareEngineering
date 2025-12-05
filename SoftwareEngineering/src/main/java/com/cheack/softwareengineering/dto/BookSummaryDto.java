// src/main/java/com/cheack/softwareengineering/dto/BookSummaryDto.java
package com.cheack.softwareengineering.dto;

import com.cheack.softwareengineering.entity.Book;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookSummaryDto {

    private Long id;
    private String name;
    private String author;
    private String image;
    private String publisher;
    private String isbn;

    public static BookSummaryDto from(Book book) {
        if (book == null) {
            return null;
        }
        return BookSummaryDto.builder()
                .id(book.getId())
                .name(book.getName())
                .author(book.getAuthor())
                .image(book.getImage())
                .publisher(book.getPublisher())
                .isbn(book.getIsbn())
                .build();
    }
}
