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

//서평 삭제
export async function deleteReview(reviewId) {
  try {
    const res = await axiosInstance.delete(`/reviews/${reviewId}`);

    // 204면 res.data 없음
    return {
      ok: true
    };
  } catch (err) {
    const code = err.response?.data?.code;

    return {
      ok: false,
      error: code || "UNKNOWN_ERROR"
    };
  }
}

async function mapReview(apiReview) {
  return {
    reviewId: apiReview.reviewId,
    title: apiReview.title,
    excerpt: apiReview.textExcerpt || "",       
    starRating: apiReview.starRating,
    createdAt: apiReview.createdAt,
    likeCount: apiReview.likeCount,
    commentCount: apiReview.commentCount,
    likedByViewer: apiReview.likedByViewer,
    profileImage:apiReview.profileImage,
    nickname: apiReview.nickname,
    username: apiReview.username,
    avgStar: apiReview.avgStar,
    book:{
      bookId: apiReview.bookId,
      title: apiReview.bookName,
      author: apiReview.bookAuthor,
      image: apiReview.bookImage,
    }
  }
}
//최신 피드
export async function fetchLatestFeed({ page = 0, size = 8, sort = "createdAt,desc" }) {
  try {
    const res = await axiosInstance.get("/feed/latest", {
      params: { page, size, sort },
    });
    const reviews=res.data.content;
    const mappedReviews=await Promise.all(reviews.map(mapReview));
    return {
      content: mappedReviews,
      totalPages: res.data.totalPages,
      totalElements: res.data.totalElements
    };
  } catch (err) {
    console.error("최신 피드 불러오기 오류:", err);
    return { content: [], error: err.response?.data?.message };
  }
}

// username 가 팔로잉한 사용자들의 리뷰만 필터링
export async function fetchFollowingFeed({ page = 0, size = 8, sort = "createdAt,desc" }) {
  try {
    const res = await axiosInstance.get("/feed/following", {
      params: { page, size, sort },
    });
    const reviews=res.data.content;
    const mappedReviews=await Promise.all(reviews.map(mapReview));
    return{
      content: mappedReviews,
      totalPages: res.data.totalPages,
      totalElements: res.data.totalElements
    }
    
  } catch (err) {
    console.error("팔로잉 피드 불러오기 오류:", err);
    return { content: [], error: err.response?.data?.message };
  }
}

// 서평 상세 조회
export async function fetchReviewDetail(reviewId) {
  try{
    const res=await axiosInstance.get(`/reviews/${reviewId}`);
    return{
      ok: true,
      ...res.data,
    };
  }catch(err){
    const code=err.response?.data?.code;
    if(code==="REVIEW_NOT_FOUND"){
      throw{ok: false, error: "REVIEW_NOT_FOUND"}
    }
    if(code==="FORBIDDEN"){
      throw{ok: false, error:"FORBIDDEN"};
    }
    if(code=="INTERNAL_SERVER_ERROR"){
      throw{ok: false, error:"INTERNAL_SERVER_ERROR"};
    }
    
  }
}

//좋아요 수 조회
export async function fetchReviewLikeCount(reviewId) {
  try {
    const res = await axiosInstance.get(`/reviews/${reviewId}/likes/count`);
    return res.data.likeCount; // 응답: { likeCount: number }
  } catch (err) {
    const code = err.response?.data?.code;
    if (code === "REVIEW_NOT_FOUND") throw "REVIEW_NOT_FOUND";
    if (code === "FORBIDDEN") throw "FORBIDDEN";
    throw "UNKNOWN_ERROR";
  }
}

//내 서평 목록 조회
export async function getMyReviews({
  page = 0,
  size = 20,
  visibility = "ALL",
  status = "PUBLISHED",
  sort = "createdAt,desc",
}) {
  try {
    const res = await axiosInstance.get("/reviews/me", {
      params: {
        page,
        size,
        visibility,
        status,
        sort,
      },
    });
    
    return {
      success: true,
      content: res.data.content,
      totalPages: res.data.totalPages,
      totalElements: res.data.totalElements,
    };
  } catch (err) {
    console.error("내 서평 목록 조회 오류:", err);

    return {
      success: false,
      content: [],
      error: err.response?.data?.message || "서평 조회 오류",
      code: err.response?.data?.code,
    };
  }
}

//서평 좋아요
export async function likeReview(reviewId) {
  try {
    const res = await axiosInstance.post(`/reviews/${reviewId}/likes`);

    // 201 Created 또는 204 No Content → 성공
    return { ok: true };
  } catch (err) {
    const code = err.response?.data?.code || "UNKNOWN_ERROR";
    return { ok: false, code };
  }
}

//서평 좋아요 취소
export async function unlikeReview(reviewId) {
  try {
    await axiosInstance.delete(`/reviews/${reviewId}/likes`);
    return { ok: true };
  } catch (err) {
    const code = err.response?.data?.code || "UNKNOWN_ERROR";
    return { ok: false, code };
  }
}

//특정 서평 좋아요 여부
export async function fetchReviewLikeStatus(reviewId) {
  try {
    const res = await axiosInstance.get(`/reviews/${reviewId}/likes/status`);
    return { ok: true, liked: res.data.liked };
  } catch (err) {
    const code = err.response?.data?.code || "UNKNOWN_ERROR";
    return { ok: false, code };
  }
}


//좋아요한 서평 목록 조회
export async function getLikedReviews({ page = 0, size = 10, sort = "likedAt,desc" } = {}) {
  try {
    const res = await axiosInstance.get("/me/likes/reviews", {
      params: { page, size, sort },
    });

    return {
      ok: true,
      content: res.data.content, // 리뷰 배열
      totalElements: res.data.totalElements,
      totalPages: res.data.totalPages,
    };
  } catch (err) {

    return {
      ok: false,
      error: "UNKNOWN_ERROR",
      content: [],
    };
  }
}

//댓글 목록 조회
export async function fetchComments(reviewId, { page = 0, size = 10, sort = "createdAt,asc" } = {}) {
  try {
    const res = await axiosInstance.get(`/reviews/${reviewId}/comments`, {
      params: { page, size, sort },
    });

    return {
      ok: true,
      ...res.data, // content, pageable 등
    };
  } catch (err) {
    const code = err.response?.data?.code;

    return {
      ok: false,
      error: code || "UNKNOWN_ERROR",
    };
  }
}

//댓글 작성
export async function postComment(reviewId, text) {
  try {
    const res = await axiosInstance.post(`/reviews/${reviewId}/comments`, {
      text,
    });

    return {
      ok: true,
      commentId: res.data.commentId,
    };
  } catch (err) {
    const code = err.response?.data?.code;

    return {
      ok: false,
      error: code || "UNKNOWN_ERROR",
    };
  }
}

