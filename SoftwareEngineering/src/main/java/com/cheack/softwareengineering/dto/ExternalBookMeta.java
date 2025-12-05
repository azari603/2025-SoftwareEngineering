package com.cheack.softwareengineering.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

/**
 * 외부(네이버 등) 도서 메타 응답을 내부에서 쓰기 좋게 정리한 DTO
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExternalBookMeta {

    private String title;          // 책 제목
    private String imageUrl;       // 표지 이미지 URL
    private String author;         // 저자명(여러 명이면 가공해서 전달)
    private String intro;          // 소개/설명
    private String publisher;      // 출판사
    private String isbn;           // ISBN (문자열)
    private LocalDate publicationDate; // 출판일 (가능하면 yyyy-MM-dd 로 변환)
}
