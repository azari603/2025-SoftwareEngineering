import { dummyBooks } from "../mocks/dummyBooks"
import axiosInstance from "./axiosInstance";


//isbn 기반 검색
export async function getBookByISBN(isbn){
    try{
      const res=await axiosInstance.get(`/books/isbn/${isbn}`);
      return{
        ok: true,
        book: res.data,
      };
    }catch(err){
      console.error("ISBN 조회 중 오류:",err);
      const code=err.response?.data?.code??"UNKNOWN_ERROR";
      const message=err.response?.data?.message??"도서 조회 중 오류"
      return{
        ok: false,
        code,
        message,
      };
    }
}

//bookId 기반 검색, 책 상세 조회
export async function fetchBookDetail(bookId) {
  try {
    const res = await axiosInstance.get(`/books/${bookId}`);

    // 성공
    return {
      ok: true,
      ...res.data,
    };

  } catch (err) {
    const code = err.response?.data?.code;

    if (code === "BOOK_NOT_FOUND") {
      throw { ok: false, error: "BOOK_NOT_FOUND" };
    }

    throw { ok: false, error: "UNKNOWN_ERROR" };
  }
}

//도서별 서평 목록 조회
export async function fetchReveiwByBook({
  bookId, page=0, size=3, sort="createdAt, desc"
}){
  try {
    const res = await axiosInstance.get(`/reviews/books/${bookId}`, {
      params: { page, size, sort },
    });

    return {
      ok: true,
      data: res.data,         // content, totalElements 등 페이지 정보 포함
    };
  } catch (err) {
    console.error("도서 서평 목록 조회 중 오류", err);
    return {
      ok: false,
      error: err.response?.data?.message || "서평 목록 조회 오류",
    };
  }
}

//유사한 도서 추천
export async function fetchSimilarBooks({bookId, page=0, size=10}){
  try{
    const res=await axiosInstance.get(`/books/${bookId}/similar`,{
      params: {page, size},
    });
    const rawBooks=res.data?.content??[];
    const books=rawBooks.map((b)=>({
      id:b.bookId,
      name:b.name,
      author:b.author,
      image:b.imageUrl,
      avgStar: b.avgStar,
      reveiwCount: b.reviewCount,
    }));
    return {
      ok: true,
      books, // BookCard 목록 그대로 반환
    };

  } catch (err) {
    console.error("유사 도서 조회 오류", err);

    return {
      ok: false,
      error: err.response?.data?.message || "유사 도서 조회 오류",
    };
  }
}

//인기 도서 (비로그인)
export async function fetchPopularBooks({page=0, size=10}){
  try {
    const res = await axiosInstance.get("/recommendations/popular", {
      params: { page, size },
    });
    const rawBooks=res.data?.content??[];
    const books=rawBooks.map((b)=>({
      id:b.bookId,
      name:b.name,
      author:b.author,
      image:b.imageUrl,
      avgStar: b.avgStar,
      reveiwCount: b.reviewCount,
    }));
    return {
      ok: true,
      books, // BookCard 목록 그대로 반환
    };
  } catch (err) {
    console.error("인기 도서 불러오기 오류:", err);
    return { books: [], error: err.response?.data?.message };
  }
}

//개인 추천 도서
export async function fetchPersonalizedBooks({ page = 0, size = 10 }) {
  try {
    const res = await axiosInstance.get("/recommendations/me", {
      params: { page, size },
    });
    const rawBooks=res.data?.content??[];
    const books=rawBooks.map((b)=>({
      id:b.bookId,
      name:b.name,
      author:b.author,
      image:b.imageUrl,
      avgStar: b.avgStar,
      reveiwCount: b.reviewCount,
    }));
    return {
      ok: true,
      books, // BookCard 목록 그대로 반환
    };
  } catch (err) {
    console.error("개인화 추천 도서 불러오기 오류:", err);
    return { books: [], error: err.response?.data?.message };
  }
}

//(임시) 추천 도서 리스트 요청
export async function getRecommendBooks(){
    const recommended=dummyBooks
    return {books:recommended}
}

//책 검색
export async function searchBooks({q="", page=1, size=10}){
  try{
    const res=await axiosInstance.get("/search/books",{
      params: {
        q,
        page: page-1,
        size,
      }
    });

    const data=res.data;
    
      return{
        books: data.content,
        totalCount: data.totalElements,
        isPaged: true,
      };
    
  }catch(err){
    console.error("책 검색 중 오류", err);
    return{
      books: [],
      totalCount: null,
      isPaged: true,
      error: err.response?.data?.message||"검색 오류 발생",
    };
  }
}

export const getMyLibraryBooks = async (type) => {
  // 실제 API 연결 시 fetch로 교체
  return new Promise((resolve) => {
    setTimeout(() => {
      resolve({ success: true, books: dummyBooks });
    }, 300); // 로딩 딜레이 시뮬레이션
  });
};


//책 상태 지정/변경
export async function setBookStatus(bookId, status) {
  try {
    const res = await axiosInstance.put(`/library/books/${bookId}/status`, {
      status,
    });

    return {
      success: true,
      status: res.data.status,       // 현재 상태
      updatedAt: res.data.updatedAt, // 백엔드 응답 명세
    };
  } catch (err) {
    console.error("책 상태 설정 오류:", err);

    return {
      success: false,
      error: err.response?.data?.message || "책 상태 설정 오류",
      code: err.response?.data?.code,
    };
  }
}

//책 상태 해제
export async function clearBookStatus(bookId) {
  try {
    await axiosInstance.delete(`/library/books/${bookId}/status`);

    return {
      success: true,
    };
  } catch (err) {
    console.error("책 상태 해제 오류:", err);

    return {
      success: false,
      error: err.response?.data?.message || "책 상태 해제 오류",
      code: err.response?.data?.code,
    };
  }
}

//특정 책 상태 조회
export async function getMyBookStatus(bookId) {
  try {
    const res = await axiosInstance.get(`/books/${bookId}/reading-status`);

    return {
      ok: true,
      data: res.data, 
    };
  } catch (err) {
    console.error("읽기 상태 조회 오류", err);
    return {
      ok: false,
      error: err.response?.data?.message || "읽기 상태 조회 실패",
    };
  }
}


//상태별 내 서재 조회
export async function getBooksByStatus({ status, page = 0, size = 20 }) {
  try {
    const res = await axiosInstance.get("/library/books", {
      params: { status, page, size, sort: "updatedAt,desc" },
    });
  const rawBooks = res.data.content || [];

    const mappedBooks = rawBooks.map((b) => ({
      id: b.bookId,             // BookCard에서 쓰는 id
      name: b.bookName,         // 책 제목
      author: b.bookAuthor,     // 저자
      image: b.bookImage,       // 이미지 URL
      status: b.status,         // 상태
      readingStatusId: b.readingStatusId, // 필요하면 유지
      userId: b.userId,         // 참고용
    }));

    return {
      success: true,
      books: mappedBooks,
      totalElements: res.data.totalElements,
      totalPages: res.data.totalPages,
    };
  } catch (err) {
    console.error("내 서재 조회 오류:", err);

    return {
      success: false,
      books: [],
      error: err.response?.data?.message || "내 서재 조회 오류",
      code: err.response?.data?.code,
    };
  }
}

//서평 수정
export async function updateReview(reviewId, payload) {
  try {
    const res = await axiosInstance.patch(`/reviews/${reviewId}`, payload);
    return res; // status: 204
  } catch (err) {
    console.error("서평 수정 실패:", err);
    throw err;
  }
}
