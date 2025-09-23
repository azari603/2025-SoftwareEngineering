import React, { useRef } from "react";
import BookCard from "../BookCard/BookCard";
import "./BookList.css";

const BookList = ({ books }) => {
  const listRef = useRef(null);

  // 오른쪽 버튼 클릭 시 → 스크롤 이동
  const scrollRight = () => {
    if (listRef.current) {
      listRef.current.scrollBy({ left: 300, behavior: "smooth" });
    }
  };

  // 왼쪽 버튼 클릭 시 ← 스크롤 이동
  const scrollLeft = () => {
    if (listRef.current) {
      listRef.current.scrollBy({ left: -300, behavior: "smooth" });
    }
  };

  return (
    <div className="booklist-container">
      {/* 왼쪽 버튼 */}
      <button className="scroll-button left" onClick={scrollLeft}>
        ◀
      </button>

      {/* 카드 리스트 */}
      <div className="booklist-wrapper" ref={listRef}>
        {books.map((book, idx) => (
          <BookCard
            key={idx}
            title={book.title}
            author={book.author}
            imageUrl={book.imageUrl}
          />
        ))}
      </div>

      {/* 오른쪽 버튼 */}
      <button className="scroll-button right" onClick={scrollRight}>
        ▶
      </button>
    </div>
  );
};

export default BookList;
