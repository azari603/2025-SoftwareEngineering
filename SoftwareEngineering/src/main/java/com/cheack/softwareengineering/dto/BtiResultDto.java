// src/main/java/com/cheack/softwareengineering/dto/BtiResultDto.java
package com.cheack.softwareengineering.dto;

import com.cheack.softwareengineering.bti.BtiType;
import com.cheack.softwareengineering.entity.BookBTI;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BtiResultDto {

    /** ER, ES, ... */
    private String code;

    /** 두 글자 + 형 (따뜻형, 공감형...) */
    private String label;

    /** 짧은 설명 */
    private String description;

    public static BtiResultDto fromCode(String code) {
        BtiType type = BtiType.fromCode(code);
        return BtiResultDto.builder()
                .code(type.getCode())
                .label(type.getLabel())
                .description(type.getDescription())
                .build();
    }

    public static BtiResultDto fromEntity(BookBTI entity) {
        return fromCode(entity.getResultType());
    }
}
