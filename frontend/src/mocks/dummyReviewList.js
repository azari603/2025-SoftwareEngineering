import { dummyBooks } from "./dummyBooks";
import default_profile from "../assets/profile_img.png";

// 랜덤 ID 생성
function makeRandomId() {
  return Math.random().toString(36).substring(2, 10);
}

// 랜덤 선택 함수
const pick = (arr) => arr[Math.floor(Math.random() * arr.length)];
const randomRating = () => Math.ceil(Math.random() * 5);

// 샘플 문구
const TITLES = [
  "감동적인 책", "생각이 깊어지는 시간", "가볍게 읽기 좋아요",
  "몰입감 최고", "여운이 남는 책", "현실에 도움 되는 내용",
];

const PREVIEWS = [
  "처음에는 평범한 에세이겠거니 하고 읽기 시작했는데, 페이지를 넘길수록 저자의 문장에 묘하게 끌려 들어갔다. 단순히 ‘좋아 보이는 말’을 모아둔 글이 아니라, 스스로의 경험과 감정을 끝까지 끌어내서 쓰려고 한 흔적이 보였다. 특히 삶의 불확실성과 인간관계에서 느끼는 외로움에 대한 부분은 나와 너무 비슷해서 몇 번이고 다시 읽었다.책을 읽고 난 후에 가장 크게 느낀 점은 ‘문장 하나가 사람의 생각을 얼마나 멀리까지 이끌 수 있는가’였다. 읽는 동안은 마음이 무겁기도 했고, 때로는 위로받는 느낌도 들었다. 누군가는 어둡다고 할지 모르지만, 오히려 나는 이런 진솔한 서술이 더 오래 남는다.마지막 장을 덮고 나서도 한참 동안 눈을 감고 생각에 잠기게 만드는 책이었다.",
  "처음엔 그냥 퇴근길에 가볍게 읽으려고 골랐는데, 의외로 가족과 기억에 관한 이야기가 핵심이라 점점 몰입도가 높아졌다. 인물들의 관계가 복잡하지 않아서 부담 없이 읽히는데, 중반부부터 슬며시 감정선을 자극하는 대목들이 몇 개 있었다.특히 주인공이 잊고 있었던 과거의 순간을 마주하는 장면은 너무 자연스럽게 흘러가서 더 울컥했다. 억지로 울리려는 문장 없이 담백하게 흐르는데도 마음을 건드리는 힘이 있었다.마지막에는 조용하게 따뜻해지는 엔딩이라, 다 읽고 나서도 오래 여운이 남았다.전체적으로 ‘무겁지 않은 감정 드라마’를 찾는 사람에게 추천하고 싶다. 읽고 나면 괜히 소중한 사람에게 연락해보고 싶어지는 그런 책이다.",
  "요즘 자기계발서가 너무 많아서 다 비슷하다고 느끼고 있었는데, 이 책은 조금 달랐다. 단순히 방법론만을 나열하는 게 아니라, 왜 그런 행동이 필요한지 뇌과학·심리학적 근거를 기반으로 설명해주는 점이 신뢰가 갔다. 설명도 어렵지 않아서 술술 읽힌다.읽으면서 가장 좋았던 점은 ‘작은 습관 하나가 하루의 감정을 얼마나 바꾸는가’를 실제 사례로 보여준 부분이다. 나는 특히 아침 루틴 파트가 도움이 됐다.책에서 제시하는 방법들이 과장되어 있지 않고, 당장 오늘부터 적용할 수 있는 것들이라 실천하기 쉬웠다.읽고 나면 스스로에게 좀 더 친절해지는 느낌이 든다. 요즘 자기관리나 일상 정리가 잘 안 되는 사람이라면 이 책이 좋은 출발점이 될 것 같다.",
];

const READ_DATES = ["2024.01.03", "2023.11.15", "2024.02.02", "읽은 날짜 미상"];

export function makeDummyReviews(count = 8, currentUser, { withBook = true } = {}) {
  return Array.from({ length: count }, (_, i) => {
    const book = dummyBooks[i % dummyBooks.length];

    return {
      id: `review_${i + 1}`,

      // 유저
      user: {
        id: i % 2 === 0 ? currentUser.id : makeRandomId(),
        nickname: i % 2 === 0 ? currentUser.nickname : pick(["sunny", "mint", "readwithme"]),
        profileImg: default_profile
      },

      // 리뷰 메타정보
      date: "13시간 전",
      likes: Math.floor(Math.random() * 20),  // 좋아요 랜덤

      title: `${book.title} - ${pick(TITLES)}`,
      preview: pick(PREVIEWS),
      content: `${pick(PREVIEWS)}\n\n자세한 내용이 이어집니다...`,

      // 책 정보
      book: {
        id: book.id,
        title: book.title,
        author: book.author,
        image: book.image,

        rating: randomRating(),       // 평균 별점
        ratingCount: Math.floor(Math.random() * 100) + 5,
        myRating: randomRating(),
        readPeriod: pick(READ_DATES)
      },
    };
  });
}
