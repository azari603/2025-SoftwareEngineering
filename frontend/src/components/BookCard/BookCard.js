import "./BookCard.css";

const BookCard = ({ title, author, imageUrl }) => {
  return (
    <div className="book-card">
      <div className="book-image">
        {imageUrl ? (<img src={imageUrl} alt={title} />) : (<div className="placeholder" />
)}
      </div>
      <div className="book-info">
        <div className="book-title">{title || "책제목"}</div>
        <div className="book-author">{author || "저자이름"}</div>
      </div>
    </div>
  );
};

export default BookCard;
