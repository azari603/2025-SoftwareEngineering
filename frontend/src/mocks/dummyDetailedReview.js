import profileImg from "../assets/profile_img.png"
import { dummyBooks } from "./dummyBooks";

const dummyDetailedReviews = [

  {
    reviewId: 1,
    title: "조용히",
    user: {
      username: "testuser",
      nickname: "수진",
      profileImg,
    },
    createdAt: "2025.01.14",
    visibility: "PUBLIC",
    book: {
      ...dummyBooks[0],
      rating: 4.7,
      ratingCount: 42,
      readPeriod: "2025.01.01 ~ 2025.01.07",
    },
    content: `이 책은 큰 사건 없이도 마음을 묘하게 흔드는 힘이 있었다. 
잔잔한 문장들이 이어지는데도 어느 순간 문장이 가슴을 꿰뚫고 지나가는 느낌이 들었다.

특히 주인공이 스스로를 돌아보는 장면들은 나 역시 멈춰 서서 나의 시간을 되돌아보게 만들었다.
한 번에 읽기보다는, 며칠 동안 나누어 천천히 읽을 때 진가가 드러나는 책이라고 생각한다.

읽는 동안 숨이 차지도 않고, 그렇다고 지루하지도 않았다.  
그저 아주 좋은 온도의 물에 몸을 담근 것처럼 편안하고 따뜻했다.`,
    likeCount: 82,
    commentCount: 31,
    myRating: 5,
  },

  {
    reviewId: 2,
    title: "한 페이지마다 밑줄 긋고 싶은 문장들",
    user: {
      username: "ssari",
      nickname: "서현",
      profileImg,
    },
    createdAt: "2025.01.02",
    visibility: "PUBLIC",
    book: {
      ...dummyBooks[1],
      rating: 4.2,
      ratingCount: 51,
      
      readPeriod: "2024.12.28 ~ 2024.12.30",
    },
    content: `너무나도 공감되는 문장들이 많아서, 어느새 책이 포스트잇으로 가득해버렸다.
작가가 독자의 마음을 알고 있는 것 같은 느낌이 드는 문장이 굉장히 많았다.

특히 인간 관계에 대한 통찰은, 가볍게 넘기기엔 아깝고 오래 생각해보고 싶어지는 문장들 투성이었다.
그냥 좋은 책을 넘어서 ‘지금의 나에게 꼭 필요한 책’이었다.`,
    likeCount: 24,
    commentCount: 6,
    myRating: 4,
  },

 
  {
    reviewId: 3,
    title: "읽다 보니 나를 마주하게 되는 책",
    user: {
      username: "mint",
      nickname: "빙봉",
      profileImg,
    },
    createdAt: "2025.02.01",
    visibility: "PUBLIC",
    book: {
      ...dummyBooks[2],
      rating: 4.9,
      ratingCount: 78,
      
      readPeriod: "2025.01.20 ~ 2025.01.22",
    },
    content: `아무런 기대 없이 책을 펼쳤다가 정신없이 빠져들어버렸다. 
주인공이 느끼는 혼란과 성장, 그리고 사소한 감정 변화까지 너무 현실적으로 와닿았다.

마지막 장을 덮는 순간, 내가 조금 더 단단해진 느낌을 받았다.
올해 읽은 책 중 단연 최고였다.`,
    likeCount: 40,
    commentCount: 10,
    myRating: 5,
  },

  
  {
    reviewId: 4,
    title: "읽는 내내 마음이 아팠지만 소중했던 시간",
    user: {
      username: "booklover",
      nickname: "책애호가",
      profileImg,
    },
    createdAt: "2025.01.18",
    visibility: "PUBLIC",
    book: {
      ...dummyBooks[3],
      rating: 4.4,
      ratingCount: 33,
      
      readPeriod: "2025.01.10 ~ 2025.01.12",
    },
    content: `이 책은 결코 가볍게 읽을 수 없는 주제를 다루고 있다.  
하지만 그럼에도 불구하고 끝까지 읽지 않을 수 없었다.

한참 동안 여운이 남아서 책장을 덮고도 생각이 계속 이어졌다.
사람이 가진 상처, 회복, 그리고 용기에 대해 다시 생각해보게 하는 책이다.`,
    likeCount: 55,
    commentCount: 14,
    myRating: 4,
  },

  
  {
    reviewId: 5,
    title: "평범한 일상이 빛나는 순간들",
    user: {
      username: "sunny",
      nickname: "선희",
      profileImg,
    },
    createdAt: "2025.01.26",
    visibility: "PUBLIC",
    book: {
      ...dummyBooks[4],
      rating: 4.0,
      ratingCount: 18,
      
      readPeriod: "2025.01.22 ~ 2025.01.23",
    },
    content: `아무 사건 없는 일상조차도 이렇게 따뜻하게 담아낼 수 있구나 싶었다.
작가의 시선과 문체가 너무 좋아서 페이지를 넘길 때마다 기분이 맑아졌다.

평범한 하루가 얼마나 소중한지,  
그리고 사람 사이의 소소한 마음들이 얼마나 귀한지 다시 느낄 수 있었다.`,
    likeCount: 18,
    commentCount: 2,
    myRating: 4,
  },


  {
    reviewId: 6,
    title: "몰입해서 단숨에 읽어버렸다",
    user: {
      username: "aria",
      nickname: "아리아",
      profileImg,
    },
    createdAt: "2025.01.28",
    visibility: "PUBLIC",
    book: {
      ...dummyBooks[5],
      rating: 4.6,
      ratingCount: 41,
      
      readPeriod: "2025.01.25 ~ 2025.01.27",
    },
    content: `이 책은 도입부부터 독자의 시선을 붙잡는다.  
중간중간 나오는 반전 요소들도 흥미로웠고,  
무엇보다 캐릭터들이 생생해서 놓기가 힘들 정도였다.

앉은 자리에서 거의 다 읽었다.
정말 오랜만에 이런 몰입감을 맛봤다.`,
    likeCount: 33,
    commentCount: 7,
    myRating: 5,
  },


  {
    reviewId: 7,
    title: "생각보다 훨씬 깊은 이야기",
    user: {
      username: "luna",
      nickname: "루나",
      profileImg,
    },
    createdAt: "2025.01.30",
    visibility: "PUBLIC",
    book: {
      ...dummyBooks[6],
      rating: 4.1,
      ratingCount: 29,
      
      readPeriod: "2025.01.20 ~ 2025.01.21",
    },
    content: `처음에는 가볍게 읽을 수 있는 책이라고 생각했는데,  
읽다 보니 생각보다 훨씬 깊고 진지한 메시지가 담겨 있었다.

마지막 부분의 문장은 오래도록 기억에 남을 것 같다.
다시 한 번 읽어보고 싶은 책이다.`,
    likeCount: 12,
    commentCount: 1,
    myRating: 4,
  },
];
export default dummyDetailedReviews;
