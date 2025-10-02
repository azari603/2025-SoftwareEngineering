//tage는 임시로 지정해 놓은 태그 -> 추후 인공지능 넣을 때 태그로 책 추천 기능
// src/mocks/quizResults.js
const quizResults = {
  future: {
    type: "미래지향적인 독서가",
    nickname: "Future Reader",
    tags: ["아이디어", "통찰", "변화"],
    description:
      "당신은 늘 새로운 가능성을 상상하고, 현실에 안주하기보다 더 나은 미래를 그려 나가는 독서가예요. 책 속에서 혁신적인 아이디어와 변화의 힌트를 발견하며, 지식을 실생활에 적용하는 것을 즐깁니다. 자기계발서, 사회·과학 관련 에세이, 미래학 서적에서 영감을 많이 받습니다.",
    image: "/results/result_future.png",
  },
  romance: {
    type: "감성적인 독서가",
    nickname: "Emo Reader",
    tags: ["로맨스", "감성", "공감"],
    description:
      "당신은 이야기를 통해 마음을 울리고 공감을 나누는 것을 가장 중요하게 여깁니다. 인물의 감정선과 인간관계를 깊이 들여다보며, 독서가 단순한 정보 습득이 아니라 감정적 교류라는 사실을 잘 알고 있어요. 따뜻한 소설, 로맨스, 감성적인 수필과 시집에서 진정한 만족을 느낍니다.",
    image: "/results/result_romance.png",
  },
  thriller: {
    type: "논리적인 독서가",
    nickname: "Logic Reader",
    tags: ["추리", "스릴러", "분석"],
    description:
      "당신은 치밀하게 짜인 구조와 반전의 묘미를 즐기는 독서가입니다. 단서를 분석하고 복잡한 사건을 추적하는 과정에서 짜릿함을 느끼며, 논리적 사고와 문제 해결에 강점을 보입니다. 추리소설, 스릴러, 철학적 미스터리 같은 책들이 당신의 두뇌를 가장 즐겁게 자극합니다.",
    image: "/results/result_thriller.png",
  },
  fantasy: {
    type: "상상력 풍부한 독서가",
    nickname: "Dreamer",
    tags: ["창의성", "모험", "판타지"],
    description:
      "당신은 현실의 틀을 벗어나 무궁무진한 상상의 세계를 탐험하는 데 매력을 느낍니다. 새로운 세계관, 마법과 모험, 독창적인 캐릭터들이 당신의 마음을 끌어당깁니다. 판타지 소설, 모험담, SF 세계관을 다룬 책에서 가장 큰 기쁨을 찾으며, 독서를 통해 상상력의 날개를 마음껏 펼칩니다.",
    image: "/results/result_fantasy.png",
  },
  history: {
    type: "역사를 좋아하는 독서가",
    nickname: "Historian",
    tags: ["사실", "배움", "과거"],
    description:
      "당신은 과거를 통해 현재를 이해하고 미래를 비추려는 독서가입니다. 단순한 사실의 나열보다 그 속에 담긴 맥락과 교훈을 중요하게 생각하며, 역사 속 인물의 선택과 사회의 흐름에서 깊은 통찰을 얻습니다. 전기, 역사소설, 인문학 서적이 당신의 독서 여정을 풍요롭게 합니다.",
    image: "/results/result_history.png",
  },
  science: {
    type: "탐구형 독서가",
    nickname: "Explorer",
    tags: ["지식", "탐구", "과학"],
    description:
      "당신은 세상의 원리를 탐구하고 논리적 근거를 통해 사고를 확장하는 것을 즐기는 독서가예요. 새로운 지식과 발견을 책에서 찾아내며, 이를 일상과 학문에 연결시키는 능력이 뛰어납니다. 과학 교양서, 철학적 과학 서적, 데이터와 실험을 다루는 책에서 큰 즐거움을 얻습니다.",
    image: "/results/result_science.png",
  },
};

export default quizResults;
