export const toggleLocalLikedReview = (reviewId) => {
  const liked = JSON.parse(localStorage.getItem("likedReviews")) || [];
  if (liked.includes(reviewId)) {
    // 이미 눌러져 있으면 제거
    const updated = liked.filter((id) => id !== reviewId);
    localStorage.setItem("likedReviews", JSON.stringify(updated));
  } else {
    // 새로 추가
    liked.push(reviewId);
    localStorage.setItem("likedReviews", JSON.stringify(liked));
  }
};

export const getLocalLikedReviews = () => {
  return JSON.parse(localStorage.getItem("likedReviews")) || [];
};

export const isReviewLiked = (reviewId) => {
  const liked = JSON.parse(localStorage.getItem("likedReviews")) || [];
  return liked.includes(reviewId);
};