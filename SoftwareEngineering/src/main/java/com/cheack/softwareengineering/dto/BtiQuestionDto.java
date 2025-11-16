// src/main/java/com/cheack/softwareengineering/dto/BtiQuestionDto.java
package com.cheack.softwareengineering.dto;

import com.cheack.softwareengineering.bti.BtiQuestion;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BtiQuestionDto {

    private int number;
    private String text;
    private String optionA;
    private String optionB;
    private String optionC;

    public static BtiQuestionDto from(BtiQuestion question) {
        return BtiQuestionDto.builder()
                .number(question.getNumber())
                .text(question.getText())
                .optionA(question.getOptionA())
                .optionB(question.getOptionB())
                .optionC(question.getOptionC())
                .build();
    }
}
