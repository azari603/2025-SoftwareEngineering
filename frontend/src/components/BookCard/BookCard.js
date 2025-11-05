import { forwardRef } from "react";
import { Link } from "react-router-dom";
import "./BookCard.css";

const sizeToVars = {
  sm: { cardW: 120, imgH: 160 },
  md: { cardW: 150, imgH: 200 }, // 기본값
  lg: { cardW: 180, imgH: 260 },
};

const BookCard = forwardRef(({ book, size = "lg" }, ref) => {
  const preset = sizeToVars[size] || sizeToVars.md;

  const styleVars = {
      // 프리셋을 기본으로, 개별 값을 넘기면 그걸 우선
      "--card-w": preset.cardW + "px",
      "--img-h": preset.imgH + "px",
    };


  return (
    <Link to={`/book/${book.isbn}`} className={`book-card`} ref={ref} style={styleVars}>
      <div className="book-image">
        <img src={book.image} alt={book.title} />
      </div>
      <div className="book-info">
        <h3 className="book-title">{book.title || "책제목"}</h3>
        <p className="book-author">{book.author || "저자이름"}</p>
      </div>
    </Link>
  );
});

export default BookCard;

