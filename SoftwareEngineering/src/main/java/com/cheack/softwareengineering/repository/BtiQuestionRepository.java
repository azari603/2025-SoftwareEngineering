// src/main/java/com/cheack/softwareengineering/repository/BtiQuestionRepository.java
package com.cheack.softwareengineering.repository;

import com.cheack.softwareengineering.bti.BtiQuestion;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Repository
public class BtiQuestionRepository {

    private final List<BtiQuestion> questions;

    public BtiQuestionRepository() {
        List<BtiQuestion> list = new ArrayList<>();

        // Q1
        list.add(new BtiQuestion(
                1,
                "책을 고를 때 가장 끌리는 건?",
                "인물의 감정과 관계가 잘 묘사된 이야기",
                "현실에서 바로 써먹을 수 있는 지식/정보",
                "기발한 상상력이나 세계관이 돋보이는 스토리"
        ));

        // Q2
        list.add(new BtiQuestion(
                2,
                "독서 후 가장 기억에 남는 건?",
                "등장인물의 감정 변화와 위로되는 장면",
                "새로 배운 개념, 팩트, 논리 구조",
                "새로운 세계관, 설정, 상상된 장면"
        ));

        // Q3
        list.add(new BtiQuestion(
                3,
                "서점에서 가장 먼저 가는 코너는?",
                "소설/에세이 코너(감정·이야기 중심)",
                "자기계발/인문/비즈니스 코너(지식 중심)",
                "판타지/SF/미스터리 코너(세계관/설정 중심)"
        ));

        // Q4 (A→E, B→I, C→T)
        list.add(new BtiQuestion(
                4,
                "어떤 문장이 더 끌려?",
                "“읽고 나니 마음이 따뜻해졌다.”",
                "“읽고 나니 새로운 시야가 열렸다.”",
                "“읽고 나니 머릿속 퍼즐이 맞춰진 느낌이다.”"
        ));

        // Q5
        list.add(new BtiQuestion(
                5,
                "현실에서 책을 어떻게 활용하고 싶어?",
                "일상을 버티게 해주는 감정적 위로",
                "사람과 사회를 이해하는 공감 능력",
                "현실을 잠깐 잊게 해주는 완전 다른 세계"
        ));

        // Q6
        list.add(new BtiQuestion(
                6,
                "이 중 더 끌리는 문장은?",
                "“오늘 하루를 버틸 힘을 주는 문장”",
                "“세상을 보는 관점을 바꿔주는 문장”",
                "“완전히 다른 차원으로 데려가는 문장”"
        ));

        // Q7
        list.add(new BtiQuestion(
                7,
                "주인공에게 어떤 일이 벌어지면 좋겠어?",
                "힘들었지만 결국 치유와 회복을 경험함",
                "사회/관계의 진실을 깨닫고 성장함",
                "알 수 없는 세계로 모험을 떠남"
        ));

        // Q8
        list.add(new BtiQuestion(
                8,
                "완독 후 가장 만족스러운 감정은?",
                "“위로받고 공감받은 느낌”",
                "“새로운 통찰과 관점을 얻은 느낌”",
                "“다른 세계를 다녀온 모험 후 여운”"
        ));

        this.questions = Collections.unmodifiableList(list);
    }

    public List<BtiQuestion> findAll() {
        return questions;
    }
}
