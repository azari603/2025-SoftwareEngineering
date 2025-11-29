import { useEffect } from "react";
import { useAuth } from "../../context/AuthContext";
import StarRate from "../StarRate/StarRate";
import "./BookInfoSection.css";
import Button from "../Button/Button";
import * as BookAPI from "../../api/bookAPI"
import { useState } from "react";
import LoginModal from "../Modal/LoginModal/LoginModal";

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
  const safeIntro = book?.intro?.trim() || "등록된 소개가 없습니다.";
  const [showLoginModal, setShowLoginModal]=useState(false);

  //첫 로드시 내 읽기 상태 조회
  useEffect(() => {
    if (!isLoggedIn||!book) {
      setStatus("none");
      return;
    }

    async function loadStatus() {
      const res = await BookAPI.getMyBookStatus(book.id);
      if (res.ok) {
        if (res.data.hasStatus && res.data.status) {
          const clientStatus = SERVER_TO_CLIENT[res.data.status];
          setStatus(clientStatus);
        } else {
          setStatus("none");
        }
      }
    }
    loadStatus();
  }, [book.id, isLoggedIn]);

  //상태 변경 버튼 클릭
  const handleStatusChange = async (newStatus) => {
    if(!isLoggedIn){
      setShowLoginModal(true);
      return
    }
    const serverNew=CLIENT_TO_SERVER[newStatus];
  
    if (status === newStatus) {
    // 이미 선택된 상태 → 해제
    const res = await BookAPI.clearBookStatus(book.id);
    if (res.success) setStatus("none");
    return;
  }

  // 상태 설정
  const res = await BookAPI.setBookStatus(book.id, serverNew);
  if (res.success) {
    setStatus(newStatus);
  }
  };

  return (
    <section className="book-info-section">
      <div className="book-info-wrapper">
          {/* 책 표지 */}
        <div className="book-cover">
          <img src={book.image} alt={book.name} />
        </div>

        {/* 책 정보 */}
        <div className="book-info-meta">
          <div className="book-meta-wrapper">
            <h2 className="book-title">{book.name}</h2>
            <p className="book-info-author">{book.author}</p>
            <p className="book-info-pub">
              {book.publisher || "출판사 미상"} · {book.publicationDate || "출판일 미상"}
            </p>
          </div>
          

          <div className="book-rating-action-wrapper">
            {/* 별점 (더미 데이터) */}
          <div className="book-rating">
            <StarRate className="start-rate" value={book.avgStar} readOnly={true}/>
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
          <Button variant="filled" to={`/write/review?id=${book.id}`} size="small">서평 쓰기</Button> 
      </div>
      
      <div className="book-description">
          <h3>책 소개</h3>
          <p>{safeIntro}</p>
      </div>
      {showLoginModal && <LoginModal onClose={() => setShowLoginModal(false)} />}
    </section>
    
  );
}
