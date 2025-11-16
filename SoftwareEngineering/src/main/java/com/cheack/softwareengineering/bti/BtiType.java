// src/main/java/com/cheack/softwareengineering/bti/BtiType.java
package com.cheack.softwareengineering.bti;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum BtiType {

    ER("ER", "따뜻형", "현실 속 감정·위로 중심"),
    ES("ES", "공감형", "사람·관계·사회 감정 공감"),
    EW("EW", "몽환형", "감성적 상상·판타지 선호"),
    TR("TR", "실용형", "현실 지식·논리 중심"),
    TS("TS", "통찰형", "사회 구조·본질 분석"),
    TW("TW", "설정형", "치밀한 세계관·룰 선호"),
    IR("IR", "새로움형", "현실 속 새로운 시각 탐색"),
    IS("IS", "관점형", "사회·관계 재해석"),
    IW("IW", "모험형", "상상 세계 탐험·모험 선호");

    private final String code;
    private final String label;
    private final String description;

    public static BtiType fromCode(String code) {
        for (BtiType type : values()) {
            if (type.code.equalsIgnoreCase(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown BBTI code: " + code);
    }
}
