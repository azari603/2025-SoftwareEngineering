// src/main/java/com/cheack/softwareengineering/dto/BookUpsertCmd.java
package com.cheack.softwareengineering.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookUpsertCmd {

    private String name;
    private String image;
    private String author;
    private String intro;
    private String publisher;
    private String isbn;
    private LocalDate publicationDate;
}
