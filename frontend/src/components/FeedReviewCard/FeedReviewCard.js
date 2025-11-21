import "./FeedReviewCard.css";
import { FaStar } from "react-icons/fa";
import StarRate from "../StarRate/StarRate";
import { FaHeart } from "react-icons/fa";
import { FaRegCommentDots } from "react-icons/fa6";
import { useNavigate } from "react-router-dom";

export default function FeedReviewCard({ review }) {
  const {
    title,
    excerpt,
    starRating,
    createdAt,
    author,
    book,
    likeCount,
    commentCount,
  } = review;

  const formattedDate = createdAt
    ? createdAt.slice(0, 10).replace(/-/g, ".")
    : "";

    const navigate=useNavigate();
    const handleClick=()=>{
      navigate(`/review/${review.reviewId}`);
    };

  return (
    <div className="feed-review-card" onClick={handleClick}>
      {/* HEADER */}
      <div className="feed-review-header">
        <img
          src={author.profileImageUrl}
          alt=""
          className="feed-profile-img"
        />

        <div className="feed-writer-info">
          <div className="feed-writer-nickname">{author.nickname}</div>
          <div className="feed-writer-id">@{author.username}</div>
        </div>

        <div className="feed-date">{formattedDate}</div>
      </div>

      {/* REVIEW STAR RATING (이 서평의 별점) */}
      <div className="feed-stars">
        <StarRate value={starRating} readOnly={true}/>
      </div>

      {/* REVIEW TITLE & EXCERPT */}
      <div className="feed-title">{title}</div>
      <div className="feed-excerpt">{excerpt}</div>

      {/* BOOK BOX */}
      <div className="feed-book-box">
        <img src={book.imageUrl} alt="" className="feed-book-img" />

        <div className="feed-book-info">
          <div className="feed-book-top-row">
            <div className="feed-book-title">{book.name}</div>
            <div className="feed-book-author">{book.author}</div>
          </div>
          <div className="feed-book-meta-row">
            <span className="feed-book-rating">
              <FaStar className="star-icon" /> {book.averageRating.toFixed(1)}
            </span>
          </div>
        </div>
        <div className="feed-book-read-period">{book.readPeriod}</div>
      </div>

      {/* FOOTER */}
      <div className="feed-review-footer">
        <div className="footer-item">
          <FaHeart className="footer-icon heart-icon" />
          <span>{likeCount}</span>
        </div>

        <div className="footer-item">
          <FaRegCommentDots className="footer-icon comment-icon" />
          <span>{commentCount}</span>
        </div>
      </div>
    </div>
  );
}