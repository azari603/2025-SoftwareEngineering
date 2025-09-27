import { dummyBooks } from "./dummyBooks";

// 샘플 문구
const TITLES = [
  "감동적인 책", "생각이 깊어지는 시간", "가볍게 읽기 좋아요",
  "몰입감 최고", "여운이 남는 책", "현실에 도움 되는 내용",
];
const SUBTITLES = [
  "인생이 바뀐 순간", "밤새 읽게 됐어요", "한 줄 한 줄이 박힌다",
  "", "", "다시 읽고 싶은 책",
];
const PREVIEWS = [
  "읽는 내내 나를 돌아보게 만들었고, 책장을 덮고도 오랫동안 생각이 이어졌습니다.",
  "짧지만 임팩트 있는 문장들이 인상적이고, 실생활에 바로 적용해볼 수 있었어요.",
  "시간 가는 줄 모르고 읽었습니다. 주변 사람들에게도 추천하고 싶은 책이에요.",
  "처음엔 평범해 보였지만, 뒤로 갈수록 몰입도가 장난 아니었습니다.",
  "잔잔한 위로를 받는 느낌. 힘들 때 꺼내 읽고 싶은 문장들이 많았어요.",
];
const USERS = ["booklover01", "readwithme", "sunny", "mint", "wave", "haru", "dawn"];

const pick = (arr) => arr[Math.floor(Math.random() * arr.length)];
const ratingRandom = () => Math.ceil(Math.random() * 5); // 1~5

/* ithBook=true면 각 리뷰에 book 정보를 자동으로 반영
 */
export function makeDummyReviews(count = 8, { withBook = true } = {}) {
  return Array.from({ length: count }, (_, i) => {
    const review = {
      id: String(i + 1),
      title: pick(TITLES),
      subtitle: pick(SUBTITLES),
      rating: ratingRandom(),
      user: pick(USERS),
      preview: pick(PREVIEWS),
    };

    if (withBook) {
      // dummyBooks가 5권이면, 6번째 리뷰는 다시 1번째 책을 재사용
      review.book = dummyBooks[i % dummyBooks.length];
    }

    return review;
  });
}

export const dummyReviews = makeDummyReviews(8, { withBook: true });
