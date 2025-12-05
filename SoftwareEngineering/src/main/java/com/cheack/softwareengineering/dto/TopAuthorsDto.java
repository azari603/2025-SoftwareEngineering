package com.cheack.softwareengineering.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TopAuthorsDto {

    private List<AuthorStat> authors;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AuthorStat {
        private String author;
        private long reviewCount;
    }
}
