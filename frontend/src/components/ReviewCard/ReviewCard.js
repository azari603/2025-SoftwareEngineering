import { Link } from "react-router-dom";
import "./ReviewCard.css";
import starIcon from "../../assets/star.png"

const ReviewCard = ({ review, variant = "basic" }) => {
  return (
    <Link to={`/review/${review.id}`} className={`review-card ${variant}`}>
        {/*책 정보 포함*/}
      {variant === "withBook" && review.book && (
        <div className="review-book-section">
        <img
          src={review.book.image}
          alt={review.book.title}
          className="review-book-image"
        />
        <h4 className="review-book-title">{review.book.title}</h4>
        <p className="review-book-author">{review.book.author}</p>
      </div>
      )}

      <div className="review-body">
        <div className="review-header">
          <h3 className="review-title">{review.title} {review.subtitle}</h3>
          <span className="review-rating"> 
            <img src={starIcon} alt="별점" className="review-star"/>
            {review.rating}/5</span>
        </div>
        <p className="review-user">{review.user}</p>
        <p className="review-preview">
          {review.preview.length > 80
            ? review.preview.slice(0, 80) + "... 더 보기"
            : review.preview}
        </p>
      </div>
    </Link>
  );
};

export default ReviewCard;
