import { coverImages } from "./coverImages";

const titles = [
  "월요일 수요일 토요일",
  "다정한 사람이 이긴다",
  "실패를 통과하는 일",
  "세상은 실제로 어떻게 돌아가는가",
  "어른의 행복은 조용하다",
  "안녕이라 그랬어",
  "행복할 거야 이래도 되나 싶을 정도로",
];

const author = [
  "페트라 펠리니",
  "이해인",
  "박소령",
  "바츨라프 스밀",
  "태수",
  "김애란",
  "일홍",
];

export const dummyBooks = titles.map((t, i) => ({
  isbn: String(i + 1),
  title: t,                          
  author: author[i%author.length],
  image: coverImages[i % coverImages.length], 
}));