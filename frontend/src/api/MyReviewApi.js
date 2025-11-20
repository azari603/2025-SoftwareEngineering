import { getLocalLikedReviews } from "../utils/likeStorage";
import { dummyReviews } from "../mocks/dummyReviews";


// 임시 API (나중에 fetch로 바꾸면 됨)
export async function getMyReviews(currentUser) {
  if (!currentUser) return [];
  return dummyReviews.filter((r) => r.user.id === currentUser.id);

  // 나중에는 아래처럼 변경:
  // const res = await fetch('/api/my/reviews')
  // return res.json() 
}

export async function getLikedReviews(dummyReviews) {
  const likedIds = getLocalLikedReviews();
  return dummyReviews.filter((r) => likedIds.includes(r.id));
}