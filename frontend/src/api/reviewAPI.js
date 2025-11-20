import {dummyReviews} from "../mocks/dummyReviews";
import dummyDetailedReviews from "../mocks/dummyDetailedReview";

//최신 피드 (임시)
export const getLatestReviews = async () => {
  return dummyReviews;
};

// userId 가 팔로잉한 사용자들의 리뷰만 필터링 (임시)
export const getFollowingReviews = async (userId) => {
  return dummyReviews.filter((r) => r.writer.following === true);
};

// 서평 상세 조회(임시)
export async function fetchReviewDetail(reviewId) {
  return new Promise((resolve, reject) => {
    setTimeout(() => {
      // 숫자 비교를 위해 Number 변환
      const id = Number(reviewId);
      const review = dummyDetailedReviews.find(r=>r.reviewId===id);
      if(!review){
        reject({error:"REVIEW_NOT_FOUND"});
        return;
      }
      // PRIVATE 접근 제어
      // 실제에서는 AuthContext.user.username을 활용해야 함
      const currentUser = "testuser"; // 임시

      if (review.visibility === "PRIVATE" && review.user.username !== currentUser) {
        reject({ error: "FORBIDDEN" });
        return;
      }

      // 백엔드 명세 + book 정보 포함한 구조 반환
      resolve({
        reviewId: review.reviewId,
        title: review.title,
        content: review.content,
        rating: review.rating,
        createdAt: review.createdAt,
        visibility: review.visibility,

        // 작성자 정보
        user: {
          username: review.user.username,
          nickname: review.user.nickname,
          profileImg: review.user.profileImg,
        },

        // 책 정보 (명세에는 없지만 프론트에서 필요)
        book: review.book,

        // 백엔드 명세 필드
        likeCount: review.likeCount,
        commentCount: review.commentCount,
      });
    }, 400);
  });
}