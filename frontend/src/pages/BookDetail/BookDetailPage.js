import { useParams } from "react-router-dom";
import {useContext, useEffect, useState} from "react"
import { dummyBooks } from "../../mocks/dummyBooks";
import {dummyReviews} from "../../mocks/dummyReviews"
import BookInfoSection from "../../components/BookInfo/BookInfoSection";
import BookList from "../../components/BookList/BookList"
import ReviewList from "../../components/ReviewList/ReviewList"
import * as BookAPI from "../../api/bookAPI"
import "./BookDetailPage.css"
import { LayoutContext } from "../../context/LayoutContext";
import * as ReviewAPI from "../../api/reviewAPI"


export default function BookDetailPage(){
    const {bookId}=useParams();
    const [book, setBook]=useState(null)
    const [recommended, setRecommended]=useState([]);
    const [reviews, setReviews] = useState([]); //이 책의 서평
   
    const { setFooterColor } = useContext(LayoutContext);
    
    //책 정보 요청
    useEffect(()=>{
        (async () => {
            const res=await BookAPI.fetchBookDetail(bookId);
            setBook(res);
        })();
    },[bookId]);

    //이 책의 서평
    useEffect(()=>{
        async function loadReviews(){
            const res=await BookAPI.fetchReveiwByBook({
                bookId, page: 0, size: 10, sort: "createdAt,desc"
            });
            if(res.ok){
                setReviews(res.data.content);
            }else{
                console.error("도서별 서평 조회 오류",res.error);
            }
        }
        loadReviews()
    },[bookId]);
    

    //추천 도서 요청
    useEffect(() => {
        if (!book) return;

        (async () => {
            const res = await BookAPI.fetchSimilarBooks({
            bookId,
            page: 0,
            size: 10,
        });

            if (res.ok) {
            setRecommended(res.books);
            } else {
            console.error("유사 도서 조회 오류", res.error);
            setRecommended([]);
            }
        })();
    }, [book]);

    useEffect(() => {
        setFooterColor("#FDFBF4"); // 흰색 테마
    }, []);

    if(!book) return <p>로딩중..</p>
    return(
            <div className="book-detail-page">
                <div className="book-detail-card">
                    <BookInfoSection book={book}/>
                    <div className="review-wrapper">
                        <h3>이 책의 서평</h3>
                        <ReviewList reviews={reviews} mode="carousel" visibleCount={3} variant=""/>
                    </div>

                    <div className="book-wrapper">
                        <h3><span className="book-title-span">{book.name}</span>과 비슷한 책</h3>
                        <BookList books={recommended} mode="carousel" visibleCount={4} cardSize="lg"/>
                    </div>
                    
                </div>
                
            </div>
    )

}