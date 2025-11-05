import { dummyBooks } from "../mocks/dummyBooks"
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