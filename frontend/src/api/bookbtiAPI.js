// src/api/bookbtiApi.js
import axiosInstance from "./axiosInstance";

const BASE = "/bookbti";

const bookbtiApi = {
  // 질문 목록 보기
  getQuestions: () =>
    axiosInstance.get(`${BASE}/questions?version=1`),

  // 세션 생성
  createSession: () =>
    axiosInstance.post(`${BASE}/sessions`),

  // 답변 제출
  sendAnswer: (sessionId, choice) =>
    axiosInstance.post(`${BASE}/sessions/${sessionId}/answers`, {
      choice:choice
    }),

  // 되돌리기
  undo: (sessionId) =>
    axiosInstance.post(`${BASE}/sessions/${sessionId}/undo`),

  // 종료 및 결과 산출
  finish: (sessionId) =>
    axiosInstance.post(`${BASE}/sessions/${sessionId}/finish`),

  // 결과 상세 조회
  getResult: (resultId) =>
    axiosInstance.get(`${BASE}/results/${resultId}`),

  // 결과 기반 추천
  getRecommendations: (resultId, page = 0, size = 10) =>
    axiosInstance.get(`${BASE}/results/${resultId}/recommendations`, {
      params: { page, size },
    }),
};

export default bookbtiApi;
