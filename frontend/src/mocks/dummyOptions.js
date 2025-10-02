// 두 카테고리 동시에 점수 부여 (multi)

const dummyOptions = [
  {
    id: 1,
    text: "책을 읽으며 가장 흥미로운 순간은?",
    options: [
      { text: "역사적 사건이 인물의 감정을 뒤흔드는 장면", multi: { romance: 1, history: 1 } },
      { text: "과학적 발견이 새로운 상상을 불러일으킬 때", multi: { science: 1, fantasy: 1 } },
      { text: "반전이 미래를 다시 생각하게 만드는 순간", multi: { thriller: 1, future: 1 } },
    ],
  },
  {
    id: 2,
    text: "독서를 통해 얻고 싶은 경험에 가장 가까운 것은?",
    options: [
      { text: "인물의 감정 속에서 과거의 교훈을 배우는 것", multi: { romance: 1, history: 1 } },
      { text: "논리적인 사건 전개가 새로운 아이디어를 주는 것", multi: { thriller: 1, future: 1 } },
      { text: "현실적 지식이 상상력과 연결되는 것", multi: { science: 1, fantasy: 1 } },
    ],
  },
  {
    id: 3,
    text: "독서 후 가장 오랫동안 남는 것은?",
    options: [
      { text: "인물의 감정이 사회적 맥락과 맞닿은 여운", multi: { romance: 1, history: 1 } },
      { text: "새로운 지식이 미래에 대한 통찰을 준 경험", multi: { science: 1, future: 1 } },
      { text: "반전과 모험이 상상력을 자극한 순간", multi: { thriller: 1, fantasy: 1 } },
    ],
  },
  {
    id: 4,
    text: "책을 읽으며 가장 설레는 순간은?",
    options: [
      { text: "마음이 움직이고 그 속에서 교훈을 찾을 때", multi: { romance: 1, history: 1 } },
      { text: "사건이 터져 나오며 미래가 보일 때", multi: { thriller: 1, future: 1 } },
      { text: "상상 속 장면이 현실적 설명으로 빛날 때", multi: { science: 1, fantasy: 1 } },
    ],
  },
  {
    id: 5,
    text: "책 속에서 당신을 가장 몰입하게 하는 요소는?",
    options: [
      { text: "사랑과 모험이 어우러진 여정", multi: { romance: 1, fantasy: 1 } },
      { text: "논리적 사건 전개와 과학적 근거", multi: { thriller: 1, science: 1 } },
      { text: "역사적 배경에서 드러나는 미래의 교훈", multi: { history: 1, future: 1 } },
    ],
  },
  {
    id: 6,
    text: "책을 읽고 난 뒤 당신의 모습은?",
    options: [
      { text: "인물들의 감정을 곱씹으며 여운을 느낀다", category: "romance", score: 1 },
      { text: "현실 문제에 적용할 아이디어를 떠올린다", category: "future", score: 1 },
      { text: "과거 사건과 현재 사회를 연결해 생각한다", category: "history", score: 1 },
    ],
  },
  {
    id: 7,
    text: "책을 읽을 때 가장 기대하는 부분은?",
    options: [
      { text: "주인공의 감정선과 성장", category: "romance", score: 1 },
      { text: "새로운 지식과 사실의 발견", category: "science", score: 1 },
      { text: "스토리 속 반전과 긴장감", category: "thriller", score: 1 },
    ],
  },
  {
    id: 8,
    text: "당신이 좋아하는 독서 공간은?",
    options: [
      { text: "창밖 풍경이 보이는 카페", multi: { romance: 1, fantasy: 1 } },
      { text: "도서관의 고전책 코너", multi: { history: 1, future: 1 } },
      { text: "조용하고 집중되는 서재", multi: { thriller: 1, science: 1 } },
    ],
  },
  {
    id: 9,
    text: "책을 선택할 때 가장 큰 영향을 주는 요소는?",
    options: [
      { text: "표지와 디자인에서 오는 감성", category: "romance", score: 1 },
      { text: "리뷰와 평가 점수", category: "future", score: 1 },
      { text: "저자의 전문성과 배경지식", category: "science", score: 1 },
    ],
  },
  {
    id: 10,
    text: "소설 속 인물과 대화할 수 있다면?",
    options: [
      { text: "사랑과 감정을 나누는 인물", category: "romance", score: 1 },
      { text: "역사적 사건의 증인이 된 인물", category: "history", score: 1 },
      { text: "미래 기술을 상상하는 인물", category: "future", score: 1 },
    ],
  },
];

export default dummyOptions;
