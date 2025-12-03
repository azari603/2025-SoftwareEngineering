// src/main/java/com/cheack/softwareengineering/bti/BtiType.java
package com.cheack.softwareengineering.bti;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public enum BtiType {

    ER("ER", "따뜻형", "현실 속 감정·위로 중심",
            List.of("에세이", "힐링", "서정소설")),
    ES("ES", "공감형", "사람·관계·사회 감정 공감",
            List.of("휴먼드라마", "사회소설", "인간관계")),
    EW("EW", "몽환형", "감성적 상상·판타지 선호",
            List.of("로맨스판타지", "감성판타지", "서정판타지")),
    TR("TR", "실용형", "현실 지식·논리 중심",
            List.of("자기계발", "심리학", "경제경영")),
    TS("TS", "통찰형", "사회 구조·본질 분석",
            List.of("인문학", "사회과학", "역사")),
    TW("TW", "설정형", "치밀한 세계관·룰 선호",
            List.of("하드SF", "미스터리", "세계관소설")),
    IR("IR", "새로움형", "현실 속 새로운 시각 탐색",
            List.of("창의에세이", "모험논픽션", "하이브리드소설")),
    IS("IS", "관점형", "사회·관계 재해석",
            List.of("대체역사", "SF사회소설", "철학우화")),
    IW("IW", "모험형", "상상 세계 탐험·모험 선호",
            List.of("모험판타지", "스페이스오페라", "SF어드벤쳐"));

    private final String code;
    private final String label;
    private final String description;
    private final List<String> categories;

    public static BtiType fromCode(String code) {
        for (BtiType type : values()) {
            if (type.code.equalsIgnoreCase(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown BBTI code: " + code);
    }
}
