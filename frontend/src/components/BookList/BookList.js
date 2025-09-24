import { useState, useRef, useEffect } from "react";
import BookCard from "../BookCard/BookCard";
import arrowRight from "../../assets/arrowRight.png";
import arrowLeft from "../../assets/arrowLeft.png"
import "./BookList.css";

const BookList = ({ books, mode = "list", visibleCount = 3 }) => {
  const [startIndex, setStartIndex] = useState(0);
  const [rightBtnPos, setRightBtnPos] = useState(0);
  const containerRef = useRef(null);
  const lastCardRef = useRef(null);

  const handlePrev = () => {
    if (startIndex > 0) setStartIndex(startIndex - 1);
  };

  const handleNext = () => {
    if (startIndex < books.length - visibleCount) {
      setStartIndex(startIndex + 1);
    }
  };

  const visibleBooks =
    mode === "carousel"
      ? books.slice(startIndex, startIndex + visibleCount)
      : books;

  // 마지막 카드 위치 계산
  useEffect(() => {
    if (lastCardRef.current && containerRef.current) {
      const containerRect = containerRef.current.getBoundingClientRect();
      const lastCardRect = lastCardRef.current.getBoundingClientRect();

      const pos = lastCardRect.right - containerRect.left;
      setRightBtnPos(pos);
    }
  }, [visibleBooks]);

  // 캐러셀 모드일 때 버튼 표시 여부
  const showLeftBtn = mode === "carousel" && startIndex > 0;
  const showRightBtn =
    mode === "carousel" && startIndex < books.length - visibleCount;

  return (
    <div className={`book-list ${mode}`} ref={containerRef}>
      {showLeftBtn && (
        <button className="nav-button left" onClick={handlePrev}>
          <img src={arrowLeft} alt="이전" />
        </button>
      )}

      <div className="book-list-container">
        {visibleBooks.map((book, idx) => (
          <BookCard
            key={idx}
            book={book}
            ref={idx === visibleBooks.length - 1 ? lastCardRef : null}
          />
        ))}
      </div>

      {showRightBtn && (
        <button
          className="nav-button right"
          onClick={handleNext}
          style={mode === "carousel" ? { left: `${rightBtnPos - 30}px` } : {}}
        >
          <img src={arrowRight} alt="다음" />
        </button>
      )}
    </div>
  );
};

export default BookList;
