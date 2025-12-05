package com.cheack.softwareengineering.dto;

import com.cheack.softwareengineering.entity.Book;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

/**
 * Book 엔티티 한 건을 그대로 내려주거나 서비스 레이어에서 쓸 때 쓰는 DTO
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookDto {

    private Long id;
    private String name;
    private String image;
    private String author;
    private String intro;
    private String publisher;
    private String isbn;
    private LocalDate publicationDate;

    public static BookDto fromEntity(Book book) {
        if (book == null) {
            return null;
        }
        return BookDto.builder()
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
