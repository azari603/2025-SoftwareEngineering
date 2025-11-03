import { useParams } from "react-router-dom";
import {useEffect, useState} from "react"
import { dummyBooks } from "../../mocks/dummyBooks";
import {dummyReviews} from "../../mocks/dummyReviews"
import BookInfoSection from "../../components/BookInfo/BookInfoSection";
import BookList from "../../components/BookList/BookList"
import ReviewList from "../../components/ReviewList/ReviewList"
import * as BookAPI from "../../api/bookAPI"
import "./BookDetailPage.css"

export default function BookDetailPage(){
    const {isbn}=useParams();
    const [book, setBook]=useState(null)
    const [recommended, setRecommended]=useState([]);

    //책 정보 요청
    useEffect(()=>{
        (async () => {
            const res=await BookAPI.getBookByISBN(isbn);
            if(res.success) setBook(res.book);
        })();
    },[isbn]);

    //추천 도서 요청
    useEffect(()=>{
        if(!book) return;
        (async ()=>{
            const res=await BookAPI.getRecommendBooks();
            if(res.books) setRecommended(res.books);
        })();
    },[book])


    if(!book) return <p>로딩중..</p>
    return(
            <div className="book-detail-page">
                <div className="book-detail-card">
                    <BookInfoSection book={book}/>

                    

                    <div className="review-wrapper">
                        <h3>이 책의 서평</h3>
                        <ReviewList reviews={dummyReviews} mode="carousel" visibleCount={3} variant=""/>
                    </div>

                    <div className="book-wrapper">
                        <h3>{book.title}과 비슷한 책</h3>
                        <BookList books={recommended} mode="carousel" visibleCount={4} cardSize="lg"/>
                    </div>
                    
                </div>
                
            </div>
    )

}