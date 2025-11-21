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
    content: `이 책을 읽는 동안 여러 번 멈춰 서서 문장을 곱씹게 되었다.
크게 드러나는 사건은 없었지만, 그 고요함 속에서 깊은 감정의 파도가 일었다.
마치 아주 작은 돌멩이가 마음 속 호수에 떨어져 잔잔한 물결을 만들어내듯,
단순한 문장 안에 담긴 감정들이 서서히 스며들었다.

특히 인물들이 서로를 바라보는 시선과 말하지 못한 감정들이 인상 깊었다.
작가는 직접적으로 표현하지 않고, 은유와 여백을 통해 감정을 전한다.
그 덕분에 독자는 스스로 해석할 수 있는 공간을 얻게 되고,
그 과정에서 자신만의 이야기가 완성된다.

마지막 장을 덮었을 때는 왠지 모르게 따뜻하고,
조금 울컥하기도 하는 기분이 들었다.
아주 조용하지만 오래 남는 책이다.`,
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
    content: `가볍게 읽으려고 펼쳤던 책이었는데, 생각보다 훨씬 묵직한 메시지를 주었다.
작가는 인물의 성장 과정 속에서 누구나 겪는 상처와 혼란을 솔직하게 그려낸다.
인물들이 지나가는 고통스러운 순간들은 잊기 힘들 정도로 생생했고,
내 경험과 겹치는 부분이 있어서 마음이 먹먹해졌다.

특히 ‘도망치지 않고 마주하기’에 대한 메시지가 오래 남았다.
어떤 감정이든 외면한다고 사라지는 게 아니라,
오히려 더 깊은 그림자가 되어 따라붙는다는 사실을 떠올리게 했다.
이 책은 그런 감정들을 어떻게 다루어야 하는지
아주 조용하고 진심 어린 방식으로 알려준다.

읽고 나면 조금 더 단단해지는 기분이 드는 책이었다.`,
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
    content: `이 책이 특별한 이유는, 화려한 사건 없이도 ‘일상’ 그 자체를 빛나게 만든다는 점이다.
작가는 우리가 매일 지나치는 순간들을 아주 따뜻한 시선으로 바라보고,
그 안에 숨어 있는 의미를 하나씩 건져 올린다.

문장 하나하나가 일기처럼 솔직하고 잔잔해서,
읽는 동안 마음이 편안해지고 속도가 자연스레 느려졌다.
바쁘게 살아가느라 놓치고 있던 것들을
다시금 바라보게 되는 경험이었다.

책을 덮고 난 뒤, 평범한 하루가 얼마나 소중한지
그리고 작은 순간들이 얼마나 큰 위로가 될 수 있는지 깨달았다.
일상에 지친 사람이라면 꼭 한번 읽어보길 추천하고 싶은 책이다.`,
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
    content: `첫 장부터 독자의 마음을 붙잡아두는 힘이 있었다.
서사 구조가 탄탄해서 흐름이 끊기지 않고,
감정의 변화나 사건 전환이 자연스럽게 이어져 몰입감이 뛰어났다.

특히 캐릭터들의 대사가 너무 현실적이라서
마치 바로 옆에서 이야기를 듣는 것처럼 생생했다.
각 등장인물의 상처와 욕망이 입체적으로 드러나며,
독자는 어느 순간 그들의 감정에 깊이 공감하게 된다.

후반부로 갈수록 감정의 밀도가 높아져서
책을 내려놓고 잠깐 쉬어가고 싶은 순간들도 있었다.
강렬하고도 인상적인 독서 경험이었다.`,
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
    content: `작가는 삶의 여러 고민들을 매우 간결하고 깊이 있게 풀어낸다.
크게 새로운 이야기를 하는 것 같지 않지만,
익숙한 주제를 새로운 관점으로 바라보게 만드는 힘이 있다.

특히 ‘자기 자신을 이해하는 일’에 대한 통찰이 뛰어났고,
나는 읽는 동안 여러 번 밑줄을 그으며 나와 대화하는 기분을 느꼈다.
한 번 읽고 끝나는 책이 아니라,
어느 순간 다시 꺼내서 읽고 싶어지는 책이라고 느껴졌다.

잔잔하지만 오래가는 울림을 주는 이런 책이 참 좋다.`,
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
    content: `이 책의 가장 큰 매력은 문체였다.
부드럽고 차분한 문장이 계속 이어지는데,
그 리듬 자체가 독자를 자연스럽게 책 속으로 끌어들인다.
어떤 문장은 시처럼 아름답고, 어떤 문장은 일기처럼 진솔했다.

읽는 동안 시간이 천천히 흐르는 듯한 기분이 들었고,
책 속 세계가 조용히 나를 감싸는 느낌이 있었다.
특별한 사건은 없지만 분위기와 감정선이 너무 좋아서
끝까지 읽는 것이 아쉬울 정도였다.

문체를 기준으로 책을 고르는 사람이라면
꼭 한 번 읽어보길 추천하고 싶다..`,
    likeCount: 12,
    commentCount: 1,
    myRating: 4,
  },
];
export default dummyDetailedReviews;
