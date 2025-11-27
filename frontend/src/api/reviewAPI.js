import {dummyReviews} from "../mocks/dummyReviews";
import dummyDetailedReviews from "../mocks/dummyDetailedReview";
import axiosInstance from "./axiosInstance";

//서평 작성
export async function createReview(reviewData){
  try{
    const res=await axiosInstance.post("/reviews",reviewData);
    return{
      success: true,
      reviewId: res.data,
    }
  }catch(err){
    console.error("서평 작성 오류:",err);
    const code=err.response?.data?.code??"UNKNOWN_ERROR";
    const message=err.response?.data?.message??"서평 작성 중 오류";
    return{
      success:false,
      code,
      message,
    }
  }
}
//최신 피드 (임시)
export const getLatestReviews = async () => {
  return dummyReviews;
};

// username 가 팔로잉한 사용자들의 리뷰만 필터링 (임시)
export const getFollowingReviews = async (username) => {
  return dummyReviews.filter((r) => r.writer.following === true);
};

//특정 책의 서평 목록 (임시)
export async function getReviewsByBookId(bookId, page = 0, size = 10, sort = "latest") {
  await new Promise((r) => setTimeout(r, 300)); // 로딩 흉내

  // 1) PUBLIC만 필터
  let filtered = dummyReviews.filter(
    (r) => r.book.bookId === bookId && r.visibility === "PUBLIC"
  );

  // 2) 정렬
  if (sort === "latest") {
    filtered = filtered.sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt));
  }

  // 3) 페이징 처리
  const start = page * size;
  const end = start + size;
  const pageContent = filtered.slice(start, end);

  return {
    content: pageContent,
    totalCount: filtered.length,
    page,
    size,
  };
}

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
      const currentUser="testuser";

      if (review.visibility === "PRIVATE" && review.user.username !== currentUser) {
        reject({ error: "FORBIDDEN" });
        return;
      }

      // 백엔드 명세 + book 정보 포함한 구조 반환
      resolve({
        reviewId: review.reviewId,
        title: review.title,
        content: review.content,
        myRating: review.myRating,
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

//내 서평 목록 조회 (임시)
// 임시 내 서평 목록 조회 API
export async function getMyReviews(currentUserName, {
  page = 0,
  size = 20,
  visibility = "ALL",
  status = "PUBLISHED",
  sort = "createdAt,desc"
} = {}) {

  return new Promise((resolve) => {
    setTimeout(() => {
      // 1. user 필터
      let list = dummyDetailedReviews.filter(
        r => r.user.username === currentUserName
      );

      // 4. 정렬
      if (sort === "createdAt,desc") {
        list = list.sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt));
      }

      // 5. 페이징
      const start = page * size;
      const end = start + size;
      const content = list.slice(start, end);

      // 6. 목록용 데이터 형식으로 변환
      const mapped = content.map(r => ({
        reviewId: r.reviewId,
        title: r.title,
        preview: r.content.slice(0, 80) + "...",
        createdAt: r.createdAt,
        likeCount: r.likeCount,
        visibility: r.visibility,
        status: r.status,
        user: r.user,
        book: r.book,
      }));

      resolve({
        content: mapped,
        totalCount: list.length,
        page,
        size,
      });
    }, 300);
  });

}

//좋아요한 서평 목록 조회 (임시)
