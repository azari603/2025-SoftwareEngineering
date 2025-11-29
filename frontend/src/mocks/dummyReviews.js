import dummyDetailedReviews from "./dummyDetailedReview";

// 목록용 리뷰 데이터
export const dummyReviews = dummyDetailedReviews.map((r) => ({
  reviewId: r.reviewId,
  title: r.title,
  excerpt: r.content.slice(0, 200), // 미리보기 생성
  createdAt: r.createdAt,
  likeCount: r.likeCount,
  commentCount: r.commentCount,
  visibility: r.visibility,
  myRating: r.myRating,

  user: {
    username: r.user.username,
    nickname: r.user.nickname,
    profileImg: r.user.profileImg,
  },

  book: {
    bookId: r.book.bookId,
    name: r.book.name,
    author: r.book.author,
    image: r.book.image,
    rating: r.book.rating,
    ratingCount: r.book.ratingCount,
  },
}));

export default dummyReviews;