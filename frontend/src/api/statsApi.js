import axiosInstance from "../api/axiosInstance"

const BASE = "/api/v1/stats";

// 추후 토큰 자동 포함되도록 axios interceptor 사용 

//독서 통계 개요
export async function fetchOverview({ period, from, to }) {
  try {
    const res = await axiosInstance.get("/stats/me/overview", {
      params: { period, from, to },
    });
    return res.data; // 백엔드 응답 그대로 반환
  } catch (err) {
    console.error("Failed to fetch overview:", err);
    throw err;
  }
}

//이번달/올해 목표 및 진행률 조회
export async function fetchGoals(period = "month") {
  try {
    const res = await axiosInstance.get("/stats/me/goals", {
      params: { period },
    });
    return res.data;   
  } catch (err) {
    console.error("Failed to fetch goals:", err);
    throw err;
  }
}

//이번달 목표 설정
export async function updateMonthlyGoal(monthlyGoal) {
  try {
    await axiosInstance.patch("/profiles/me/goal", {
      monthlyGoal,
    });

    // 204 No Content → 성공
    return { ok: true };
  } catch (err) {
    console.error("Failed to update monthly goal:", err);
    const serverCode = err.response?.data?.code || null;

    return {
      ok: false,
      code: serverCode,
      status: err.response?.status,
    };
  }
}

//내 별점 도서
export async function fetchStars() {
  try {
    const res = await axiosInstance.get("/stats/me/stars");
    return res.data; // {0:..., 1:..., 2:..., ...}
  } catch (err) {
    console.error("Failed to fetch stars:", err);
    throw err;
  }
}

//내 상위 작가
export async function fetchTopAuthors(top = 10) {
  try {
    const res = await axiosInstance.get(`/stats/me/authors?top=${top}`);
    return res.data; // [{ name: "작가명", count: 5 }, ...]
  } catch (err) {
    console.error("Failed to fetch authors:", err);
    throw err;
  }
}

//카테코리 비율 조회?
export async function fetchCategories() {
  try {
    const res = await axiosInstance.get("/stats/me/categories");
    return res.data; // { "소설": 40, "에세이": 30, ... }
  } catch (err) {
    console.error("Failed to fetch categories:", err);
    throw err;
  }
}

//독서 타임라인 조회
export async function fetchTimeline({ granularity = "month", from, to }) {
  try {
    const res = await axiosInstance.get("/stats/me/timeline", {
      params: {
        granularity,
        from,
        to,
      },
    });
    return res.data;  
  } catch (err) {
    console.error("Failed to fetch timeline:", err);
    throw err;
  }
}