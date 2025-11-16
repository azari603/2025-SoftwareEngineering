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

    public static BookDetailDto from(Book book) {
        if (book == null) {
            return null;
        }
        return BookDetailDto.builder()
                .id(book.getId())
                .name(book.getName())
                .image(book.getImage())
                .author(book.getAuthor())
                .intro(book.getIntro())
                .publisher(book.getPublisher())
                .isbn(book.getIsbn())
                .publicationDate(book.getPublicationDate())
                .build();
    }
}
