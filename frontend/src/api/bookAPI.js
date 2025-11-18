import { dummyBooks } from "../mocks/dummyBooks"

function loadStatusMap() {
  return JSON.parse(localStorage.getItem("statusMap") || "{}");
}
function saveStatusMap(map) {
  localStorage.setItem("statusMap", JSON.stringify(map));
}

//(임시) 특정 도서 상세 정보 요청
export async function getBookByISBN(isbn){
    const book = dummyBooks.find((b)=>b.isbn===isbn)
    if(!book){
        return {sucess:false}
    }
    return {success:true, book}
}

//(임시) 추천 도서 리스트 요청
export async function getRecommendBooks(){
    const recommended=dummyBooks
    return {books:recommended}
}

//(임시) 책 검색
export async function searchBooks({query="",page,pageSize}={}){
    await new Promise((r)=>setTimeout(r,200))

    let filtered=dummyBooks;
    if(query.trim()){
        const q=query.toLowerCase();
        filtered=dummyBooks.filter(
            (book)=>
                book.title.toLowerCase().includes(q)||
            book.author.toLowerCase().includes(q)
        )
    }

    const totalCount=filtered.length;

    if(page&&pageSize){
        const start=(page-1)*pageSize;
        const end=start+pageSize;
        filtered=filtered.slice(start,end);
    }

    return {
        success: true,
        totalCount,
        books: filtered,
        isPaged: !!page&&!!pageSize
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
export async function updateBookStatus(isbn, status){
  let map=loadStatusMap();
  map[isbn]=status;
  saveStatusMap(map);
  return {success: true, status};
}

//(임시)책 상태 해제
export async function removeBookStatus(isbn) {
  let map=loadStatusMap();
  if(map[isbn]){
    delete map[isbn];
    saveStatusMap(map);
  }
  return {success: true};
}


//(임시) 상태별 내 서재 조회
export async function getBooksByStatus(status) {
  const map = loadStatusMap();

  const filteredIsbns = Object.entries(map)
    .filter(([_, s]) => s === status)
    .map(([isbn]) => isbn);

  const filteredBooks = dummyBooks.filter((b) =>
    filteredIsbns.includes(b.isbn)
  );

  const mappedBooks = filteredBooks.map((b) => ({
    isbn: b.isbn,
    title: b.title,
    author: b.author,
    image: b.image,
    publisher: b.publisher,
  }));

  return {
    success: true,
    books: mappedBooks,
    totalCount: mappedBooks.length,
  };
}
    
