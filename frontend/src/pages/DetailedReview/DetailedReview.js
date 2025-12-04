import React, { useState, useRef, useEffect, useContext } from "react";
import { useNavigate, useParams, useLocation } from "react-router-dom";
import profile_img from "../../assets/profile_img.png";
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
import * as reviewAPI from "../../api/reviewAPI"; //서평 상세 조회 API
import * as followAPI from "../../api/followAPI"
import CustomModal from "../../components/Modal/CustomModal"
import { FaStar} from "react-icons/fa";

const base=process.env.REACT_APP_BASE_URL;
function fullUrl(path) {
  if (!path) return null;
  if (path.startsWith("http")) return path;
  return `${base}/${path}`; // base 붙이기
}

const ReviewDetail = () => {
  const navigate = useNavigate();
  const { isLoggedIn, user: currentUser } = useAuth();
  const { reviewId } = useParams();
  
  /* Hook은 무조건 return보다 위! */
  const { setFooterColor } = useContext(LayoutContext);

  //리뷰 데이터
  const [review, setReview] = useState(null);
  const [loading, setLoading] = useState(true);
  const [fetchError, setFetchError] = useState(null);

  //좋아요 상태
  const [liked, setLiked] = useState(false);
  const [likeCount, setLikeCount] = useState(0);

  //댓글
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

  const [showDeleteModal, setShowDeleteModal] = useState(false);

  //책 정보
  const [book, setBook]=useState(null);

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

  //서평 상세 조회 API 호출
  useEffect(()=>{
    async function loadReview(){
      try{
        const data=await reviewAPI.fetchReviewDetail(reviewId);
        const {ok, ...reviewData}=data;
        setReview(reviewData);

        if(ok){ //리뷰 로딩 완료시 호출
          const count=await reviewAPI.fetchReviewLikeCount(reviewId); //리뷰의 좋아요 수
        setLikeCount(count);

        // 좋아요 상태
          const status = await reviewAPI.fetchReviewLikeStatus(reviewId);
          if (status.ok) {
            setLiked(status.liked);
          }

          if(!reviewData.mine){
            const followStatus=await followAPI.fetchFollowStatus(reviewData.authorUsername)
            if(followStatus.ok){
              setIsFollowing(followStatus.following);
            }
          }
        
        setBook(reviewData.book);
        }
        
      }catch(err){
        setFetchError(err.error||"UNKNOWN_ERROR");
      }finally{
        setLoading(false);
      }
    }
    loadReview()
  },[reviewId]);

  /** 로딩 */
  if (loading) {
    return <div className="card">불러오는 중...</div>;
  }

  /** 에러 처리 */
  if (fetchError) {
    return (
      <div className="card">
        {fetchError === "REVIEW_NOT_FOUND" && "서평을 찾을 수 없습니다."}
        {fetchError === "FORBIDDEN" && "비공개 서평입니다."}
        {fetchError === "INTERNAL_SERVER_ERROR" && "서평을 불러오는 중 오류가 발생했습니다."}
      </div>
    );
  }
  const nickname=review.authorNickname??"작성자"
  const profileImg=fullUrl(review.authorProfileImageUrl)??profile_img
 

  /** 내 서평 여부 */
  const isMyReview = review?.mine===true;

  /** 좋아요 클릭 시 상태 업데이트*/
  const handleLikeClick = async() => {
    if (!isLoggedIn) {
      setShowLoginModal(true);
      return;
    }

      // UI 먼저 반영
    const nextLiked = !liked;
    setLiked(nextLiked);
    setLikeCount((prev) => (nextLiked ? prev + 1 : prev - 1));

    // 실제 서버에 반영
    if (nextLiked) {
      const res = await reviewAPI.likeReview(reviewId);
      if (!res.ok) {
        // 서버 반영 실패 → UI 롤백
        setLiked(false);
        setLikeCount((prev) => prev - 1);
        alert("좋아요에 실패했습니다.");
      }
    } else {
      const res = await reviewAPI.unlikeReview(reviewId);
      if (!res.ok) {
        // 서버 반영 실패 → UI 롤백
        setLiked(true);
        setLikeCount((prev) => prev + 1);
        alert("좋아요 취소에 실패했습니다.");
      }
    }

  };

  //댓글 목록 조회
  const loadComments = async (page = currentPage - 1) => {
    const res = await reviewAPI.fetchComments(reviewId, {
      page,
      size: commentsPerPage,
    });

    if (res.ok) {
      setComments(res.content);
    } else {
      if (res.error === "FORBIDDEN") setFetchError("FORBIDDEN");
      if (res.error === "REVIEW_NOT_FOUND") setFetchError("REVIEW_NOT_FOUND");
    }
  };

  /** 댓글 등록 */
  const handleCommentSubmit = async() => {
    if (!newComment.trim()) return;

    if(!isLoggedIn){
      setShowLoginModal(true);
      return;
    }

    const res=await reviewAPI.postComment(reviewId, newComment);
    if(!res.ok){
      if(res.error==="FORBIDDEN"){
        alert("비공개 서평에는 댓글을 달 수 없습니다.");
      }else{
        alert("댓글 등록에 실패하였습니다.")
      }
      return;
    }
    await loadComments();
    setNewComment("");
    setShowAlert(true);
  };

  const handleToggleComments = async () => {
  const nextOpen = !commentsOpen;
  setCommentsOpen(nextOpen);

  if (nextOpen) {
    await loadComments();
  }
};

//서평 삭제
const handleDeleteReview = async () => {
  const res = await reviewAPI.deleteReview(reviewId);

  if (!res.ok) {
    if (res.error === "FORBIDDEN") {
      alert("본인의 서평만 삭제할 수 있습니다.");
    } else if (res.error === "REVIEW_NOT_FOUND") {
      alert("서평을 찾을 수 없습니다.");
    } else {
      alert("서평 삭제에 실패했습니다.");
    }
    return;
  }

  // 삭제 성공 → 이전 페이지로 이동
  navigate(-1); // 뒤로가기
};

//팔로우 버튼 클릭
const handleFollowClick = async () => {
  if (!isLoggedIn) {
    setShowLoginModal(true);
    return;
  }

  const target = review.authorUsername;
  if (!target) return;

  // UI update
  const next = !isFollowing;
  setIsFollowing(next);

  let res;
  if (next) {
    res = await followAPI.follow(target);
  } else {
    res = await followAPI.unfollow(target);
  }

  if (!res.ok) {
    // rollback UI
    setIsFollowing(!next);
    alert("팔로우 처리에 실패했습니다.");
  }
};

  /** JSX 렌더링 */
  return (
    <div className="card">
      <div className="review-detail">

        {/* 뒤로가기 */}
        <header className="review-header">
          <div className="menu-btn-row">
            <button className="back-btn" onClick={() => navigate(-1)}>
            <IoIosArrowBack />
            </button>
            {isMyReview && (
              <div className="user-action-area" ref={menuRef}>
                <button className="menu-btn" onClick={() => setMenuOpen(!menuOpen)}>
                  <FaEllipsisVertical />
                </button>

                {menuOpen && (
                  <div className="menu-dropdown">
                    <button className="menu-item"
                    onClick={() => navigate(`/review/edit/${reviewId}`)}>수정</button>
                    <button className="menu-item delete" onClick={() => setShowDeleteModal(true)}>삭제</button>
                  </div>
                )}
              </div>
            )}
          </div>
          
          <div className="title-profile-wrapper">
            <div className="review-title-row">
            <h1 className="review-title">{review.title}</h1>

            
          </div>

          <div className="review-user_">
            <div className="review-user__profile"
              onClick={()=>navigate(`/profile/${review.authorUsername}`)}
              style={{cursor:"pointer"}}>
              {<img src={profileImg} 
              onError={(e) => (e.target.src = profile_img)}
              alt="user" className="review-user__img" />} 
              <div className="review-user__info">
                <p className="review-user__name">{nickname}</p>
                <p className="review-user__date">{review.createdAt.split("T")[0]}</p>
              </div>
            </div>

            {!isMyReview && (
              <button
                className={`follow-btn ${isFollowing ? "following" : ""}`}
                onClick={handleFollowClick}
              >
                {isFollowing ? "팔로잉" : "+ 팔로우"}
              </button>
            )}
          </div>
          </div>

          
        </header>

        {/* 책 정보 */}
        <section className="book-info-review">
          <img src={book.image} alt={book.name} className="book-cover" />
          <div className="review-book-meta">
            <div className="book-title-review">
              <h3 className="title-review">{book.name}</h3>
              <p className="book-author">{book.author}</p>
            </div>
          
            <div className="book-rating">
              <div className="book-rating__avg">
                <FaStar className="book-rating-icon"/>{book.avgStar} <span className="avg-count">({book.reviewCount})</span>
              </div>

              <div className="book-rating__my">
                <span className="my-text">내 평점</span>
                <StarRate value={review.starRating} readOnly={true} />
              </div>
            </div>
            
          </div>
          <p className="book-read">읽은 날짜: {review.startDate} - {review.finishDate}</p>
        </section>

        <hr className="review-divider" />

        {/* 본문 */}
        <article className="review-content">
          {review.text?.split("\n").map((line, idx) => (
            <p key={idx}>{line}</p>
          ))}
        </article>

        {/* 좋아요 & 댓글 열기 */}
        <footer className="review-footer">
          <button className="like-btn" onClick={handleLikeClick}>
            <span className={`heart-wrapper ${liked ? "liked" : ""}`}>
              <FaRegHeart className="heart base" />
              <FaHeart className="heart overlay" />
            </span>
            <span>{likeCount}</span>
          </button>

          <button className={`comment-btn ${commentsOpen ? "open" : ""}`} onClick={handleToggleComments}>
          <FaRegCommentDots />
          <span>댓글 {review.commentCount}</span>
          <SlArrowRight className="comment-arrow-icon" />
        </button>
        </footer>

        {/* 댓글 영역 */}
        {commentsOpen && (
          <div className="comment-section">
            <div className="comment-list">
              {comments.length === 0 ? (
                <p className="no-comment">가장 먼저 댓글을 달아보세요!</p>
              ) : (
                comments.map((c) => (
                  <div key={c.commentId} className="comment-item">
                    <img
                      src={c.author.profileImageUrl || default_profile}
                      alt="user"
                      className="comment-user__img"
                    />
                    <div className="comment-content">
                      <p className="comment-user__name">{c.author.nickname}</p>
                      <p className="comment-text">{c.text}</p>
                      <p className="comment-date">{c.createdAt.split("T")[0]}</p>
                    </div>
                  </div>
                ))
              )}
            </div>

            <Pagination
              currentPage={currentPage}
              totalCount={comments.length}
              pageSize={commentsPerPage}
              onPageChange={async (page) => {setCurrentPage(page); await loadComments(page-1)}}
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
      {showDeleteModal && (
        <CustomModal
          title="서평 삭제"
          message={`서평을 삭제하시겠습니까?
          삭제 후에는 복구할 수 없습니다.`}
          onConfirm={() => {
            setShowDeleteModal(false);
            handleDeleteReview();
          }}
          onCancel={() => setShowDeleteModal(false)}
        />
      )}
    </div>
  );
};

export default ReviewDetail;
