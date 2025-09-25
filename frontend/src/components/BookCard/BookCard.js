import { forwardRef } from "react";
import { Link } from "react-router-dom";
import "./BookCard.css";

const BookCard = forwardRef(({ book }, ref) => {
  return (
    <Link to={`/book/${book.isbn}`} className="book-card" ref={ref}>
      <div className="book-image">
        <img src={book.image} alt={book.title} />
      </div>
      <div className="book-info">
        <h3>{book.title || "책제목"}</h3>
        <p>{book.author || "저자이름"}</p>
      </div>
    </Link>
  );
});

export default BookCard;

