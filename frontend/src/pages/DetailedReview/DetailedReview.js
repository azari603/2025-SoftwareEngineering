import React, { useState, useRef, useEffect, useContext } from "react";
import { useNavigate, useParams, useLocation } from "react-router-dom";
import "./DetailedReview.css";
import { FaHeart, FaRegHeart, FaRegCommentDots } from "react-icons/fa";
import { FaEllipsisVertical } from "react-icons/fa6";
import { SlArrowRight, SlArrowDown } from "react-icons/sl";
import { IoIosArrowBack } from "react-icons/io";
import StarRate from "../../components/StarRate/StarRate";
import { useAuth } from "../../context/AuthContext";
import LoginModal from "../../components/Modal/LoginModal/LoginModal";
import Pagination from "../../components/Pagination/Pagination";
import default_profile from "../../assets/profile_img.png";
import GenericModal from "../../components/Modal/GenericModal";
import { LayoutContext } from "../../context/LayoutContext";
import { toggleLocalLikedReview, isReviewLiked } from "../../utils/likeStorage";

const ReviewDetail = () => {
  const navigate = useNavigate();
  const { isLoggedIn } = useAuth();
  const { id } = useParams();
  const location = useLocation();

  /** review 데이터 (navigate state로 전달됨) */
  const review = location.state?.review;
  const currentUser = location.state?.currentUser;

  /** 🔥 Hook은 무조건 return보다 위!! */
  const { setFooterColor } = useContext(LayoutContext);

  const [liked, setLiked] = useState(() => isReviewLiked(`review_${id}`));
  const [likeCount, setLikeCount] = useState(review?.likes || 0);

  const [comments, setComments] = useState([]);
  const [newComment, setNewComment] = useState("");

  const [showLoginModal, setShowLoginModal] = useState(false);
  const [showAlert, setShowAlert] = useState(false);

  const [menuOpen, setMenuOpen] = useState(false);
  const [isFollowing, setIsFollowing] = useState(false);
  const menuRef = useRef(null);

  const [commentsOpen, setCommentsOpen] = useState(false);
  const [currentPage, setCurrentPage] = useState(1);
  const commentsPerPage = 5;

  /** 화면 색 변경 */
  useEffect(() => {
    setFooterColor("#FDFBF4");
  }, [setFooterColor]);

  /** 메뉴 외부 클릭 */
  useEffect(() => {
    const handleClick = (e) => {
      if (menuRef.current && !menuRef.current.contains(e.target)) {
        setMenuOpen(false);
      }
    };

    document.addEventListener("mousedown", handleClick);
    return () => document.removeEventListener("mousedown", handleClick);
  }, []);

  /** 🔥 review가 없으면 예외 반환 — Hook 아래에서 return이 가능함 */
  if (!review) return <div className="card">잘못된 접근입니다.</div>;

  const { title, user, book, preview, date } = review;

  /** 내 서평 여부 */
  const isMyReview = currentUser?.id === user.id;

  /** 좋아요 */
  const handleLikeClick = () => {
    if (!isLoggedIn) {
      setShowLoginModal(true);
      return;
    }

    toggleLocalLikedReview(`review_${id}`);
    setLiked((prev) => !prev);
    setLikeCount((prev) => (liked ? prev - 1 : prev + 1));
  };

  /** 댓글 등록 */
  const handleCommentSubmit = () => {
    if (!newComment.trim()) return;

    const now = new Date();
    const newCommentObj = {
      id: Date.now(),
      user: {
        name: currentUser?.nickname || "나",
        profileImg: currentUser?.profileImg || default_profile,
      },
      text: newComment,
      date: now.toLocaleString("ko-KR"),
    };

    setComments((prev) => [newCommentObj, ...prev]);
    setNewComment("");
    setShowAlert(true);
  };

  /** JSX 렌더링 */
  return (
    <div className="card">
      <div className="review-detail">

        {/* 뒤로가기 */}
        <header className="review-header">
          <button className="back-btn" onClick={() => navigate(-1)}>
            <IoIosArrowBack />
          </button>

          <div className="review-title-row">
            <h1 className="review-title">"{title}"</h1>

            {isMyReview && (
              <div className="user-action-area" ref={menuRef}>
                <button className="menu-btn" onClick={() => setMenuOpen(!menuOpen)}>
                  <FaEllipsisVertical />
                </button>

                {menuOpen && (
                  <div className="menu-dropdown">
                    <button className="menu-item">수정</button>
                    <button className="menu-item delete">삭제</button>
                  </div>
                )}
              </div>
            )}
          </div>

          <div className="review-user_">
            <div className="review-user__profile">
              <img src={user.profileImg} alt="user" className="review-user__img" />
              <div className="review-user__info">
                <p className="review-user__name">{user.nickname}</p>
                <p className="review-user__date">{date}</p>
              </div>
            </div>

            {!isMyReview && (
              <button
                className={`follow-btn ${isFollowing ? "following" : ""}`}
                onClick={() => setIsFollowing(!isFollowing)}
              >
                {isFollowing ? "팔로잉" : "+ 팔로우"}
              </button>
            )}
          </div>
        </header>

        {/* 책 정보 */}
        <section className="book-info-review">
          <img src={book.image} alt={book.title} className="book-cover" />
          <div className="review-book-meta">
            <h3 className="book-title-review">{book.title}</h3>
            <p className="book-author">{book.author}</p>
            <p className="book-read">읽은 날짜 : {book.readPeriod}</p>

            <div className="book-rating">
              <div className="book-rating__avg">
                ⭐{book.rating} <span className="avg-count">({book.ratingCount})</span>
              </div>

              <div className="book-rating__my">
                <span className="my-text">내 평점</span>
                <StarRate value={book.myRating} readOnly={true} />
              </div>
            </div>
          </div>
        </section>

        <hr className="review-divider" />

        {/* 본문 */}
        <article className="review-content">
          {preview?.split("\n").map((line, idx) => (
            <p key={idx}>{line}</p>
          ))}
        </article>

        <hr className="review-divider" />

        {/* 좋아요 & 댓글 열기 */}
        <footer className="review-footer">
          <button className="like-btn" onClick={handleLikeClick}>
            <span className={`heart-wrapper ${liked ? "liked" : ""}`}>
              <FaRegHeart className="heart base" />
              <FaHeart className="heart overlay" />
            </span>
            <span>{likeCount}</span>
          </button>

          <button className="comment-btn" onClick={() => setCommentsOpen(!commentsOpen)}>
            <FaRegCommentDots />
            <span>댓글 {comments.length}</span>
            {commentsOpen ? <SlArrowDown /> : <SlArrowRight />}
          </button>
        </footer>

        {/* 댓글 영역 */}
        {commentsOpen && (
          <div className="comment-section">
            <div className="comment-list">
              {comments.length === 0 ? (
                <p className="no-comment">가장 먼저 댓글을 달아보세요!</p>
              ) : (
                comments
                  .slice((currentPage - 1) * commentsPerPage, currentPage * commentsPerPage)
                  .map((c) => (
                    <div key={c.id} className="comment-item">
                      <img src={c.user.profileImg} alt="user" className="comment-user__img" />
                      <div className="comment-content">
                        <p className="comment-user__name">{c.user.name}</p>
                        <p className="comment-text">{c.text}</p>
                        <p className="comment-date">{c.date}</p>
                      </div>
                    </div>
                  ))
              )}
            </div>

            <Pagination
              currentPage={currentPage}
              totalCount={comments.length}
              pageSize={commentsPerPage}
              onPageChange={(page) => setCurrentPage(page)}
            />

            <div className="comment-input">
              {isLoggedIn ? (
                <>
                  <input
                    value={newComment}
                    onChange={(e) => setNewComment(e.target.value)}
                    placeholder="서로가 존중해지는 댓글을 작성해보세요."
                  />
                  <button onClick={handleCommentSubmit}>등록</button>
                </>
              ) : (
                <div className="login-notice" onClick={() => setShowLoginModal(true)}>
                  지금 로그인하고 댓글에 참여해보세요!
                </div>
              )}
            </div>
          </div>
        )}

        {showLoginModal && <LoginModal onClose={() => setShowLoginModal(false)} />}
        {showAlert && (
          <GenericModal
            message="댓글이 등록되었습니다."
            cancelText="확인"
            showConfirm={false}
            onCancel={() => setShowAlert(false)}
          />
        )}
      </div>
    </div>
  );
};

export default ReviewDetail;
