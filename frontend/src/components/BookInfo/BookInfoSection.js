import { useEffect } from "react";
import { useAuth } from "../../context/AuthContext";
import StarRate from "../StarRate/StarRate";
import "./BookInfoSection.css";
import Button from "../Button/Button";
import * as BookAPI from "../../api/bookAPI"
import { useState } from "react";

const CLIENT_TO_SERVER={
  want:"WISHLIST",
  reading: "READING",
  finished: "COMPLETED"
}

const SERVER_TO_CLIENT={
  WISHLIST: "want",
  READING: "reading",
  COMPLETED: "finished"
}


export default function BookInfoSection({ book }) {
  const {isLoggedIn, user} =useAuth();
  const [status, setStatus]=useState("none"); //현재 책 상태

  //책 상태 초기 로딩
  useEffect(() => {
    if (!isLoggedIn) {
      setStatus("none");
      return;
    }
    async function loadStatus(){
      const serverStatus=await BookAPI.getBookStatus(book.bookId, user.username);
      if(serverStatus){
        setStatus(SERVER_TO_CLIENT[serverStatus]);
      }else{
        setStatus("none");
      }
    }
    loadStatus();
},[book, isLoggedIn, user]);

  if (!book) return null;

  //상태 변경 버튼 클릭
  const handleStatusChange = async (newStatus) => {
    if(!isLoggedIn){
      alert("로그인이 필요합니다")
      return
    }
    const serverNew=CLIENT_TO_SERVER[newStatus];
    const serverCurrent=CLIENT_TO_SERVER[status];
    
    //같은 버튼 클릭 -> 상태 해제
    if(serverNew===serverCurrent){
      await BookAPI.removeBookStatus(book.bookId, user.username);
      setStatus("none");
      return;
    }

    //다른 버튼 클릭 -> 상태 변경
    await BookAPI.updateBookStatus(book.bookId, serverNew, user.username);
    setStatus(newStatus);
  };

  return (
    <section className="book-info-section">
      <div className="book-info-wrapper">
          {/* 책 표지 */}
        <div className="book-cover">
          <img src={book.image} alt={book.title} />
        </div>

        {/* 책 정보 */}
        <div className="book-info-meta">
          <div className="book-meta-wrapper">
            <h2 className="book-title">{book.title}</h2>
            <p className="book-author">{book.author}</p>
            <p className="book-pub">
              {book.publisher || "출판사 미상"} · {book.publishDate || "출판일 미상"}
            </p>
          </div>
          

          <div className="book-rating-action-wrapper">
            {/* 별점 (더미 데이터) */}
          <div className="book-rating">
            <StarRate className="start-rate" value={book.rating} readOnly={true}/>
          </div>

          {/* 상태 변경 버튼 */}
          <div className="book-actions">
            {["want", "reading", "finished"].map((state) => (
              <button
                key={state}
                className={`action-btn ${status === state ? "active" : ""}`}
                onClick={() => handleStatusChange(state)}
              >
                {state === "want" && "읽고 싶어요"}
                {state === "reading" && "읽는 중이에요"}
                {state === "finished" && "다 읽었어요"}
              </button>
            ))}
          </div>

          
          </div>
          
        </div>
        {/* 서평쓰기 버튼 */}
          <Button variant="filled" to={`/write/review?bookId=${book.bookId}`} size="small">서평 쓰기</Button> 
      </div>
      
      <div className="book-description">
          <h3>책 소개</h3>
          <p>{book.description}</p>
      </div>
    </section>
  );
}
