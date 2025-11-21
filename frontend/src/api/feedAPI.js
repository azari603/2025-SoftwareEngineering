// src/api/feedAPI.js
import dummyReviews from "../mocks/dummyReviews";

function makeFakeDate() {
  const now = new Date();
  const randomHours = Math.floor(Math.random() * 120); // 5일 이내
  return new Date(now.getTime() - randomHours * 3600 * 1000).toISOString();
}

const randomCommentCount = () => Math.floor(Math.random() * 20);

export async function fetchLatestFeed({ page = 0, size = 8 } = {}) {
  const dummy = dummyReviews

  const mapped = dummy.map((review) => ({
    reviewId: review.reviewId,
    title: review.title,
    excerpt: review.preview,
    starRating: review.myRating,
    createdAt: review.createdAt,

    author: {
      username: review.user.username,
      nickname: review.user.nickname,
      profileImageUrl: review.user.profileImg,
    },

    book: {
      bookId: review.book.bookId,
      name: review.book.title,
      imageUrl: review.book.image,
      author: review.book.author,                 // ✅ 작가
      averageRating: review.book.rating,          // ✅ 평균 평점
      readPeriod: review.book.readPeriod,         // ✅ 읽은 기간
    },

    likeCount: review.likeCount,
    commentCount: review.commentCount,
    myLike: Math.random() > 0.5,
  }));

  return new Promise((resolve) => {
    setTimeout(() => {
      resolve({
        content: mapped,
        page,
        size,
        totalPages: 1,
        totalElements: mapped.length,
      });
    }, 300);
  });
}