import axiosInstance from "./axiosInstance";

//팔로우 생성
export async function follow(targetUsername) {
  try {
    await axiosInstance.post(`/follows/${targetUsername}`);
    return { ok: true };
  } catch (err) {
    return {
      ok: false,
      error: err.response?.data?.code || "FOLLOW_ERROR",
    };
  }
}

//팔로우 취소
export async function unfollow(targetUsername) {
  try {
    await axiosInstance.delete(`/follows/${targetUsername}`);
    return { ok: true };
  } catch (err) {
    return {
      ok: false,
      error: err.response?.data?.code || "UNFOLLOW_ERROR",
    };
  }
}

//팔로우 상태 조회
export async function fetchFollowStatus(targetUsername) {
  try {
    const res = await axiosInstance.get(
      `/follows/${targetUsername}/status`
    );
    return {
      ok: true,
      following: res.data.following,
    };
  } catch (err) {
    return {
      ok: false,
      error: err.response?.data?.code || "FOLLOW_STATUS_ERROR",
      following: false,
    };
  }
}