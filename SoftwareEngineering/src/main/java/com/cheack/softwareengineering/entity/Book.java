// src/main/java/com/cheack/softwareengineering/entity/Book.java
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

    // 🔹 intro를 TEXT로 변경
    @Column(name = "intro", columnDefinition = "TEXT")
    private String intro;

    @Column(name = "publisher", length = 200)
    private String publisher;

    @Column(name = "isbn", length = 20, unique = true)
    private String isbn;

    @Column(name = "publication_date")
    private LocalDate publicationDate;
}