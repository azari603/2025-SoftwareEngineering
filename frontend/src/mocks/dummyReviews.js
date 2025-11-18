import { dummyBooks } from "./dummyBooks";
import default_profile from "../assets/profile_img.png";

const TITLES = [
  "감동적인 책", "생각이 깊어지는 시간", "가볍게 읽기 좋아요",
  "몰입감 최고", "여운이 남는 책", "현실에 도움 되는 내용",
];

const SUBTITLES = [
  "인생이 바뀐 순간", "밤새 읽게 됐어요", "한 줄 한 줄이 박힌다",
  "", "", "다시 읽고 싶은 책",
];

const PREVIEWS = [
  "읽는 내내 나를 돌아보게 만들었고, 책장을 덮고도 오래 생각이 이어졌어요.",
  "짧지만 임팩트 있는 문장들이 많았어요.짧지만 임팩트 있는 문장들이 많았어요.짧지만 임팩트 있는 문장들이 많았어요.짧지만 임팩트 있는 문장들이 많았어요.짧지만 임팩트 있는 문장들이 많았어요.짧지만 임팩트 있는 문장들이 많았어요.짧지만 임팩트 있는 문장들이 많았어요.짧지만 임팩트 있는 문장들이 많았어요.짧지만 임팩트 있는 문장들이 많았어요.짧지만 임팩트 있는 문장들이 많았어요.짧지만 임팩트 있는 문장들이 많았어요.짧지만 임팩트 있는 문장들이 많았어요.",
  "추천하고 싶은 책입니다.추천하고 싶은 책입니다.추천하고 싶은 책입니다.추천하고 싶은 책입니다.추천하고 싶은 책입니다.추천하고 싶은 책입니다.추천하고 싶은 책입니다.추천하고 싶은 책입니다.추천하고 싶은 책입니다.추천하고 싶은 책입니다.추천하고 싶은 책입니다.추천하고 싶은 책입니다.추천하고 싶은 책입니다.추천하고 싶은 책입니다.추천하고 싶은 책입니다.추천하고 싶은 책입니다.추천하고 싶은 책입니다.추천하고 싶은 책입니다.추천하고 싶은 책입니다.추천하고 싶은 책입니다.추천하고 싶은 책입니다.",
  "몰입감 최고였습니다. 몰입감 최고였습니다. 몰입감 최고였습니다. 몰입감 최고였습니다. 몰입감 최고였습니다. 몰입감 최고였습니다.몰입감 최고였습니다. 몰입감 최고였습니다. 몰입감 최고였습니다. 몰입감 최고였습니다. 몰입감 최고였습니다. 몰입감 최고였습니다.몰입감 최고였습니다. 몰입감 최고였습니다. 몰입감 최고였습니다. 몰입감 최고였습니다. 몰입감 최고였습니다. 몰입감 최고였습니다.몰입감 최고였습니다. 몰입감 최고였습니다. 몰입감 최고였습니다. 몰입감 최고였습니다. 몰입감 최고였습니다. 몰입감 최고였습니다.",
  "위로가 되는 책이었어요.위로가 되는 책이었어요.위로가 되는 책이었어요.위로가 되는 책이었어요.위로가 되는 책이었어요.위로가 되는 책이었어요.위로가 되는 책이었어요.위로가 되는 책이었어요.위로가 되는 책이었어요.위로가 되는 책이었어요.위로가 되는 책이었어요.위로가 되는 책이었어요.위로가 되는 책이었어요.위로가 되는 책이었어요.위로가 되는 책이었어요.위로가 되는 책이었어요.위로가 되는 책이었어요.위로가 되는 책이었어요.",
];

const USERS = [
  { nickname: "booklover01", profileImg: default_profile },
  { nickname: "readwithme", profileImg: default_profile },
  { nickname: "sunny", profileImg: default_profile },
  { nickname: "mint", profileImg: default_profile },
  { nickname: "wave", profileImg: default_profile },
];

const pick = (arr) => arr[Math.floor(Math.random() * arr.length)];
const ratingRandom = () => Math.ceil(Math.random() * 5);

export function makeDummyReviews(count = 8, { withBook = true } = {}) {
  return Array.from({ length: count }, (_, i) => {
    const user = pick(USERS);
    const book = dummyBooks[i % dummyBooks.length];

    return {
      id: `review_${i + 1}`, 

      title: pick(TITLES),
      subtitle: pick(SUBTITLES),
      preview: pick(PREVIEWS),

      likes: Math.floor(Math.random() * 30),
      date: "2시간 전",
      content: `${pick(PREVIEWS)}\n\n자세한 내용이 이어집니다...`,

      rating: ratingRandom(),

      user: {
        id: `user_${i + 1}`,
        nickname: user.nickname,
        profileImg: user.profileImg
      },

      book: withBook
        ? {
            ...book,
            rating: ratingRandom(),
            ratingCount: Math.floor(Math.random() * 200) + 10,
            myRating: ratingRandom(),
            readPeriod: "2024.01.01 ~ 2024.01.10",
          }
        : null
    };
  });
}

export const dummyReviews = makeDummyReviews(8, { withBook: true });
