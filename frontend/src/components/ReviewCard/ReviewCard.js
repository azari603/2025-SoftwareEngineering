import { forwardRef } from "react";
import { Link } from "react-router-dom";
import starIcon from "../../assets/star.png"
import "./ReviewCard.css"

const ReviewCard = forwardRef(({ review, variant = "basic" }, ref) => {
  const nickname=review.nickname||review.userId
  return (
    <Link to={`/review/${review.reviewId}`} state={{review}} className={`review-card ${variant}`} ref={ref}>
      {variant === "withBook" && review.book && (
        <div className="review-book-section">
          <img
            src={review.book.image}
            alt={review.book.name}
            className="review-book-image"
          />
          <h4 className="review-book-title">{review.book.title}</h4>
          <p className="review-book-author">{review.book.author}</p>
        </div>
      )}

      <div className="review-body">
        <div className="review-header">
          <h3 className="review-title">{review.title}</h3>
          <span className="review-rating">
            <img src={starIcon} alt="별점" className="review-star" />
            {review.starRating}/5
          </span>
        </div>
        <p className="review-user">{nickname}</p>
        <p className="review-preview">
          {review.excerpt.length > 80 ? review.excerpt.slice(0, 80) + "... 더 보기" : review.excerpt}
        </p>
      </div>
    </Link>
  );
});

export default ReviewCard;

