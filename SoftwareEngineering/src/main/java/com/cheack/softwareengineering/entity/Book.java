package com.cheack.softwareengineering.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(
        name = "books",
        indexes = {
                @Index(name = "idx_books_name", columnList = "name"),
                @Index(name = "idx_books_author", columnList = "author"),
                @Index(name = "idx_books_isbn", columnList = "isbn")
        }
)
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "book_id")
    private Long id;

    @Column(name = "name", nullable = false, length = 300)
    private String name;

    @Column(name = "image", length = 500)
    private String image;

    @Column(name = "author", length = 200)
    private String author;

    @Column(name = "intro", length = 2000)
    private String intro;

    @Column(name = "publisher", length = 200)
    private String publisher;

    /**
     * ERD에는 Long 으로 되어있지만,
     * 이미 너 클래스 다이어그램에서 isbn String 으로 쓰고 있어서 String 으로 잡았다.
     */
    @Column(name = "isbn", length = 20, unique = true)
    private String isbn;

    /**
     * 출판일은 LocalDate 정도가 적당해서 이렇게 잡았다.
     */
    @Column(name = "publication_date")
    private LocalDate publicationDate;
}
