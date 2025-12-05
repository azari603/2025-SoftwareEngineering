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
                "하루 중 가장 위로가 되는 순간은?",
                "누군가와 따뜻한 대화를 나눌 때",
                "머릿속이 정리되고 계획이 잡힐 때",
                "상상하거나 새로운 아이디어 떠오를 때"
        ));

        // Q2
        list.add(new BtiQuestion(
                2,
                "친구가 고민을 털어놓을 때 나는?",
                "감정에 공감하며 함께 이야기한다",
                "문제의 원인을 분석해서 해결책을 찾는다",
                "다양한 관점·새로운 생각을 제시한다"
        ));

        // Q3
        list.add(new BtiQuestion(
                3,
                "주말에 가장 하고 싶은 활동은?",
                "산책·카페·따뜻한 자리에서 쉬기",
                "정리·계획 세우기·능률 올릴 활동",
                "새로운 곳 탐험·취미·창작"
        ));

        // Q4
        list.add(new BtiQuestion(
                4,
                "좋아하는 사람의 성향은?",
                "감정이 따뜻하고 섬세한 사람",
                "말이 논리적이고 믿음직한 사람",
                "독창적이고 유쾌한 사람"
        ));

        // Q5
        list.add(new BtiQuestion(
                5,
                "스트레스를 받을 때 가장 먼저 하는 행동은?",
                "감정 정리하며 조용히 쉬기",
                "이유를 분석하고 해결책을 찾기",
                "다른 생각·상상을 하며 벗어나기"
        ));

        // Q6
        list.add(new BtiQuestion(
                6,
                "누군가에게 선물을 한다면?",
                "상대의 마음을 따뜻하게 할 감성적인 물건",
                "실용적이고 필요할 만한 물건",
                "특별하고 개성 있는 독특한 물건"
        ));

        // Q7
        list.add(new BtiQuestion(
                7,
                "좋아하는 말투 또는 표현 방식은?",
                "감성적이고 부드러운 말투",
                "명확하고 논리적인 말투",
                "비유·상상·창의적 표현이 담긴 말투"
        ));

        // Q8
        list.add(new BtiQuestion(
                8,
                "방을 꾸밀 때 더 끌리는 분위기는?",
                "따뜻하고 편안한 무드",
                "깔끔하고 정돈된 구조",
                "개성 있고 컨셉이 확실한 공간"
        ));

        // Q9
        list.add(new BtiQuestion(
                9,
                "영화를 볼 때 가장 집중하는 요소는?",
                "감정·분위기·배우의 감정선",
                "사건 구조·논리·메시지",
                "세계관·설정·창의적 연출"
        ));

        // Q10
        list.add(new BtiQuestion(
                10,
                "대화에서 더 중요하게 여기는 것은?",
                "감정의 진심",
                "내용의 정확성",
                "표현의 독창성"
        ));

        // Q11
        list.add(new BtiQuestion(
                11,
                "대화를 할 때 가장 관심 가는 주제는?",
                "현실적인 일상·생활 얘기",
                "사람 관계·사회 문제·문화 얘기",
                "상상·세계관·미래·우주 같은 이야기"
        ));

        // Q12
        list.add(new BtiQuestion(
                12,
                "여행을 가면 어떤 곳이 끌리나?",
                "조용한 마을·자연·현지 생활이 보이는 곳",
                "사람들 많은 도시·문화·역사 있는 곳",
                "이국적인 느낌·비현실적 풍경의 장소"
        ));

        // Q13
        list.add(new BtiQuestion(
                13,
                "유튜브에서 가장 자주 보는 영상은?",
                "브이로그·정리·라이프스타일",
                "인간관계·사회 이슈·심리·다큐",
                "미스터리·SF·세계관 분석·창작"
        ));

        // Q14
        list.add(new BtiQuestion(
                14,
                "가끔 나만의 시간을 보낼 때 하는 생각은?",
                "내 일상·현실 문제를 정리하는 편",
                "인간관계나 사회 이슈를 곱씹는 편",
                "상상 속 시나리오나 다른 세계를 생각함"
        ));

        // Q15
        list.add(new BtiQuestion(
                15,
                "카페에서 시간을 보낼 때 가장 좋은 자리는?",
                "창가, 조용하고 편안한 자리",
                "사람들 오가는 곳, 분위기 생생한 자리",
                "독특한 인테리어, 상상력 자극되는 자리"
        ));

        // Q16
        list.add(new BtiQuestion(
                16,
                "즐겨보는 콘텐츠는?",
                "현실 기반 브이로그·생활 루틴",
                "인간관계 분석·사회 이야기",
                "세계관 해설·판타지·우주 관련 영상"
        ));

        // Q17
        list.add(new BtiQuestion(
                17,
                "새로운 취미를 시작한다면 어떤 것이 가장 끌리나요?",
                "현실적인 기술·생활에 바로 도움이 되는 취미",
                "사람들과 소통하거나 함께 하는 활동",
                "창작·세계관·상상력을 발휘할 수 있는 취미"
        ));

        // Q18
        list.add(new BtiQuestion(
                18,
                "가장 오래 기억에 남는 이야기는?",
                "현실과 맞닿아 있는 진솔한 이야기",
                "인간관계 중심의 사회적 이야기",
                "판타지·미지의 세계를 탐험하는 이야기"
        ));

        // Q19
        list.add(new BtiQuestion(
                19,
                "가끔 멍을 때릴 때 떠올리는 생각은?",
                "내 일상과 해야 할 일",
                "사람 관계나 사회적 고민",
                "상상·세계관·다른 현실"
        ));

        // Q20
        list.add(new BtiQuestion(
                20,
                "가장 오래 이야기할 수 있는 주제는?",
                "현실적인 삶·일·취미",
                "사회·관계·사람 이야기",
                "상상·세계관·미래 이야기"
        ));

        this.questions = Collections.unmodifiableList(list);
    }

    public List<BtiQuestion> findAll() {
        return questions;
    }
}
