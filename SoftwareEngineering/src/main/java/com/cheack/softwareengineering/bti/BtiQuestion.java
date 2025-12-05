// src/main/java/com/cheack/softwareengineering/bti/BtiQuestion.java
package com.cheack.softwareengineering.bti;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BtiQuestion {

    private final int number;      // 1~8
    private final String text;     // 질문
    private final String optionA;  // 보기 A
    private final String optionB;  // 보기 B
    private final String optionC;  // 보기 C
}
