import axios from "axios";

const BASE = "/api/v1/stats";

// 추후 토큰 자동 포함되도록 axios interceptor 사용 

export const StatsApi = {
  getOverview(period = "month") {
    return axios.get(`${BASE}/me/overview`, {
      params: { period },
    });
  },

  getStars() {
    return axios.get(`${BASE}/me/stars`);
  },

  getTimeline() {
    return axios.get(`${BASE}/me/timeline`, {
      params: { granularity: "month" },
    });
  },

  getTopAuthors(top = 10) {
    return axios.get(`${BASE}/me/authors`, {
      params: { top },
    });
  },

  getGoals(period = "month") {
    return axios.get(`${BASE}/me/goals`, {
      params: { period },
    });
  },
};
