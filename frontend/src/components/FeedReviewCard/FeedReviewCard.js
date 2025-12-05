import "./FeedReviewCard.css";
import { FaStar } from "react-icons/fa";
import { FaRegHeart } from "react-icons/fa";
import StarRate from "../StarRate/StarRate";
import { FaHeart } from "react-icons/fa";
import { FaRegCommentDots } from "react-icons/fa6";
import { useNavigate } from "react-router-dom";
import profile_img from "../../assets/profile_img.png";

const base=process.env.REACT_APP_BASE_URL;
function fullUrl(path) {
  if (!path) return null;
  if (path.startsWith("http")) return path;
  return `${base}/${path}`; // base 붙이기
}

export default function FeedReviewCard({ review }) {
  const {
    title,
    excerpt,
    starRating,
    createdAt,
    username,
    nickname,
    profileImage,
    avgStar,
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
          src={fullUrl(profileImage)||profile_img}
          onError={(e) => (e.target.src = profile_img)}
          alt=""
          className="feed-profile-img"
        />

        <div className="feed-writer-info">
          <div className="feed-writer-nickname">{nickname}</div>
          <div className="feed-writer-id">@{username}</div>
        </div>

        <div className="feed-date">{formattedDate}</div>
      </div>

      <div className="rating-title-wrapper">
        {/* REVIEW STAR RATING (이 서평의 별점) */}
        <div className="feed-stars">
          <StarRate value={starRating} readOnly={true}/>
        </div>
        <div className="feed-title">{title}</div>
      </div>
      

      {/* REVIEW TITLE & EXCERPT */}
      
      <div className="feed-excerpt">{excerpt}</div>

      {/* BOOK BOX */}
      <div className="feed-book-box">
        <img src={book.image} alt="" className="feed-book-img" />

        <div className="feed-book-info">
          <div className="feed-book-top-row">
            <div className="feed-book-title">{book.title}</div>
            <div className="feed-book-author">{book.author}</div>
          </div>
          <div className="feed-book-meta-row">
            <span className="feed-book-rating">
              <FaStar className="star-icon" /> {avgStar}
            </span>
          </div>
        </div>
        <div className="feed-book-read-period">{book.readPeriod}</div>
      </div>

      {/* FOOTER */}
      <div className="feed-review-footer">
        <div className="footer-item">
          <FaRegHeart className="footer-icon heart-icon" />
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