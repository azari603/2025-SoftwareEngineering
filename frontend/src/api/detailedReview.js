import dummyReview from "../mocks/dummyDetailedReview";
import dummyComments from "../mocks/dummyComments";

/**
 * 리뷰 상세 정보와 댓글 목록을 가져오는 함수
 * (현재는 더미데이터 기반 / 추후 백엔드 연결 시 axios 요청으로 교체)
 */
export async function getReviewDetail(reviewId) {
  return new Promise((resolve) => {
    setTimeout(() => {
      const review=dummyReview.find((r)=>r.id===reviewId);
      resolve({
        review: review??dummyReview[0],
        comments: dummyComments,
      });
    }, 100); // 로딩 시각적 딜레이용
  });
}
