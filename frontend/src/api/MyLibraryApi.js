import { dummyBooks } from "../mocks/dummyBooks";

/**
 * 임시 Mock API - 나의 서재 관련
 * type: 'reading' | 'want' | 'finished'
 */
export const getMyLibraryBooks = async (type) => {
  // 실제 API 연결 시 fetch로 교체
  return new Promise((resolve) => {
    setTimeout(() => {
      resolve({ success: true, books: dummyBooks });
    }, 300); // 로딩 딜레이 시뮬레이션
    
  });
  
};
