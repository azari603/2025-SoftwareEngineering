import { dummyBooks } from "../mocks/dummyBooks"
import axiosInstance from "./axiosInstance";
//---임시 저장소----//
function loadStatusMap() {
  return JSON.parse(localStorage.getItem("statusMap") || "{}");
}
function saveStatusMap(map) {
  localStorage.setItem("statusMap", JSON.stringify(map));
}
//------------------//

//(임시) 특정 도서 상세 정보 요청
export async function getBookByISBN(bookId){
    const book = dummyBooks.find((b)=>b.bookId===bookId)
    return book;
}

//(임시) 추천 도서 리스트 요청
export async function getRecommendBooks(){
    const recommended=dummyBooks
    return {books:recommended}
}

//책 검색
export async function searchBooks({q="", page=0, size=10}){
  try{
    const res=await axiosInstance.get("/search/books",{
      params: {
        q,
        page,
        size,
      }
    });

    const data=res.data;
    if(Array.isArray(data)){
      return {
        books: data,
        totalCount: null,
        isPaged: false,
      };
    }

    if(data.content&&typeof data.totalElements==="number"){
      return{
        books: data.content,
        totalCount: data.totalElements,
        isPaged: true,
      };
    }
  }catch(err){
    console.error("책 검색 중 오류", err);
    return{
      books: [],
      totalCount: null,
      isPaged: false,
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


//(임시)책 상태 지정/변경
//임시 API이기 때문에 username가 필요함. 실제 연결할때는 필요없음
export async function updateBookStatus(bookId, status, username){
  const store = loadStatusMap();

  if (!store[username]) store[username] = {};
  store[username][bookId] = status;

  saveStatusMap(store);
  return status;
}

//(임시)책 상태 해제
export async function removeBookStatus(bookId, username) {
  const store = loadStatusMap();

  if (store[username] && store[username][bookId]) {
    delete store[username][bookId];
    saveStatusMap(store);
  }
}

//(필요) 특정 책 상태 조회
export async function getBookStatus(bookId, username) {
  const store = loadStatusMap();

  // 유저별 스토리지 없으면 null
  if (!store[username] || !store[username][bookId]) return null;

  return store[username][bookId];  // "WISHLIST" | "READING" | "COMPLETED"
}


//(임시) 상태별 내 서재 조회
export async function getBooksByStatus({ status, username }) {
  const store = loadStatusMap();

  // 유저 저장 공간이 없으면 빈 값 반환
  if (!store[username]) {
    return { books: [], totalCount: 0 };
  }

  // 해당 유저의 책 상태 목록
  const userMap = store[username];

  const filteredBookIds = Object.entries(userMap)
    .filter(([_, s]) => s === status)
    .map(([bookId]) => bookId);

  const filteredBooks = dummyBooks.filter((b) =>
    filteredBookIds.includes(b.bookId)
  );

  // 실제 API 구조에 맞춰 응답
  return {
    books: filteredBooks.map((b) => ({
      bookId: b.bookId,
      title: b.title,
      author: b.author,
      image: b.image,
      publisher: b.publisher,
      updatedAt: new Date().toISOString(), // 임시 값
      status,
    })),
    totalCount: filteredBooks.length,
  };
}
