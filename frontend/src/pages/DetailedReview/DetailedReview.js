import React,{useState,useRef,useEffect } from "react";
import { useNavigate, useParams } from "react-router-dom";
import "./DetailedReview.css";
import { FaHeart,FaRegHeart, FaRegCommentDots } from "react-icons/fa";
import { FaEllipsisVertical } from "react-icons/fa6";
import { SlArrowRight } from "react-icons/sl";
import { SlArrowDown } from "react-icons/sl";
import { IoIosArrowBack } from "react-icons/io";
import StarRate from "../../components/StarRate/StarRate";
import {useAuth} from "../../context/AuthContext";
import LoginModal from "../../components/Modal/LoginModal/LoginModal";
import Pagination from "../../components/Pagination/Pagination";
import { getReviewDetail } from "../../api/detailedReview";
import default_profile from "../../assets/profile_img.png";
import GenericModal from "../../components/Modal/GenericModal";
import { toggleLocalLikedReview } from "../../utils/likeStorage";

const ReviewDetail = ({ review,currentUser }) => {
  const navigate = useNavigate();
  const { isLoggedIn } = useAuth();
  const {id}=useParams();  //리뷰의 id 값 -> 현재 어떤 리뷰인지 보여줌

  const [data,setData]=useState(null);
  const [comments,setComments]=useState(null);
  const [newComment,setNewComment]=useState("");
  const [loading,setLoading]=useState(true);
  
  // 팝업창
  const [showLoginModal, setShowLoginModal] = useState(false);
  const [showAlert,setShowAlert]=useState(false);
  // 메뉴·팔로우 상태 관리
  const [menuOpen, setMenuOpen] = useState(false);
  const [isFollowing, setIsFollowing] = useState(false);
  //좋아요 상태 관리
  const[liked,setLiked]=useState(false);
  const[likeCount,setLikeCount]=useState(0);
  

  // 메뉴 영역 ref 생성
  const menuRef = useRef(null);

  // 댓글 열림 상태
  const [commentsOpen, setCommentsOpen] = useState(false);
  // 댓글 페이지별로 보여주기
  const [currentPage, setCurrentPage] = useState(1);
  const commentsPerPage=5;

  // 더미 데이터 가져오기 -> 추후 변경
  useEffect(()=>{
    const loadData=async()=>{
      try{
        const res=await getReviewDetail(id);
        setData(res.review);
        setComments(res.comments);
        //setComments([]);
        setLikeCount(res.review.likes);
      }catch(e){
        console.error(e);
      }finally{
        setLoading(false);
      }
    };
    loadData();
  },[id]);

  // 바깥 클릭 감지
  useEffect(() => {
    const handleClickOutside = (e) => {
      if (menuRef.current && !menuRef.current.contains(e.target)) {
        setMenuOpen(false);
      }
    };
    document.addEventListener("mousedown", handleClickOutside);
    return () => document.removeEventListener("mousedown", handleClickOutside);
  }, []);

  if (loading) return <div className="card">로딩중...</div>;
  if (!data) return <div className="card">리뷰 데이터를 불러올 수 없습니다.</div>;

  const { title, user, book} = data;
  const displayDate = data.modifiedDate ? `${data.modifiedDate}(수정됨)` : data.date;

  // 내 서평일 경우
  const isMyReview = currentUser?.id === user.id;
  // 내 서평이 아닐 경우
  //const isMyReview = false;

  const handleLikeClick = () => {
    if(!isLoggedIn){
      setShowLoginModal(true);
      return;
    }
    try{
      // api 연결할때 수정해야 할 부분
      toggleLocalLikedReview(id);
      setLiked((prev)=> !prev);
      setLikeCount((prev)=>(liked ? prev-1 : prev+1));
    }catch(e){
      console.error(e);
    }
  };

  const handleCommentSubmit=()=>{
    if(!newComment.trim()) return; //빈칸 방지

    const now=new Date();
    const formattedDate=now.toLocaleDateString("ko-KR",{
      year:"numeric",
      month:"2-digit",
      day:"2-digit",
    });
    const formattedTime=now.toLocaleTimeString("ko-KR",{
      hour:"2-digit",
      hour12:false,
      minute:"2-digit",
    });

    const newCommentObj={
      id:Date.now(),
      user:{
        name:currentUser?.name||"나",
        profileImg:currentUser?.profileImg || default_profile,
      },
      text: newComment,
      date:`${formattedDate} ${formattedTime}`,
    };

    setComments((prev)=>[newCommentObj,...prev]);
    setNewComment("");
    setShowAlert(true);
  }

  const toggleMenu = () => setMenuOpen(!menuOpen); //댓글 토글
  const handleFollow = () => setIsFollowing(!isFollowing); //팔로우 클릭
  // 클릭 시 토글
  const handleCommentToggle = () => setCommentsOpen(!commentsOpen);

  return (
    <div className="card">
    <div className="review-detail">
      <header className="review-header">
        <button className="back-btn" onClick={() => navigate(-1)}>
          <IoIosArrowBack />
        </button>

        {/* 제목 + 오른쪽 버튼 한 줄 */}
        <div className="review-title-row">
          <h1 className="review-title">"{title}"</h1>

          <div className="user-action-area" ref={menuRef}>
            {isMyReview && (
              <>
                <button className="menu-btn" onClick={toggleMenu}>
                  <FaEllipsisVertical />
                </button>
                {menuOpen && (
                  <div className="menu-dropdown">
                    <button className="menu-item"
                    onClick={()=> navigate("#")}>수정</button>
                    <button className="menu-item delete">삭제</button>
                  </div>
                )}
              </>
            )}
          </div>
          </div>
          <div className="review-user_">
            <div className="review-user__profile">
              <img src={user.profileImg} alt="user" className="review-user__img" />
              <div className="review-user__info">
                <p className="review-user__name">{user.name}</p>
                <p className="review-user__date">{displayDate}</p>
              </div>
            </div>
          {!isMyReview && (
            <button
              className={`follow-btn ${isFollowing ? "following" : ""}`}
              onClick={handleFollow}
            >
              {isFollowing ? "팔로잉" : "+ 팔로우"}
            </button>
          )}
        </div>
      </header>



      <section className="book-info-review">
        <img src={book.image} alt={book.title} className="book-cover" />
        <div className="book-meta">
          <h3 className="book-title">{book.title}</h3>
          <p className="book-author">{book.author}</p>
          <p className="book-read">읽은 날짜 : {book.readPeriod}</p>

          {/* 별점 표시 구역 */}
          <div className="book-rating">
            <div className="book-rating__avg">
              ⭐{book.rating}
              <span className="avg-count"> ({book.ratingCount})</span>
            </div>
            <div className="book-rating__my">
              <span className="my-text">내 평점</span>
              <StarRate value={book.myRating} readOnly={true} />
            </div>
          </div>
        </div>
      </section>

      <hr className="review-divider" />

      <article className="review-content">
        {data.content.split("\n").map((line, idx) => (
          <p key={idx}>{line}</p>
        ))}
      </article>

      <hr className="review-divider" />

      <footer className="review-footer">
        <button className="like-btn" onClick={handleLikeClick}>
          <span
            className={`heart-wrapper ${liked ? "liked" : ""}`}
          >
            <FaRegHeart className="heart base" size={16} />
            <FaHeart className="heart overlay" size={16} />
          </span>
          <span>{likeCount}</span>
        </button>
        <button className="comment-btn" onClick={handleCommentToggle}>
          <span><FaRegCommentDots/></span>
          <span>댓글 {comments.length}</span>
          {commentsOpen ? <SlArrowDown /> : <SlArrowRight />}

        </button>
      </footer>
      {commentsOpen && (
        <div className="comment-section">
          <div className="comment-list">
            {comments.length===0?(
              <p className="no-comment">가장 먼저 댓글을 달아보세요 !!</p>
            ):(
              comments
                .slice((currentPage - 1) * commentsPerPage, currentPage * commentsPerPage)
                .map((c) => (
                <div key={c.id} className="comment-item">
                  <img
                    src={c.user.profileImg}
                    alt="user"
                    className="comment-user__img"
                  />
                  <div className="comment-content">
                    <p className="comment-user__name">{c.user.name}</p>
                    <p className="comment-text">{c.text}</p>
                    <p className="comment-date">{c.date}</p>
                  </div>
                </div>
                ))
              )}
          </div>
          <div className="pagination-wrapper">
            <Pagination
              currentPage={currentPage}
              totalCount={comments.length}
              pageSize={commentsPerPage}
              onPageChange={(page) => setCurrentPage(page)}
            />
          </div>
          <div className="comment-input">
            {isLoggedIn ? (
              <>
                <input
                  type="text"
                  value={newComment}
                  onChange={(e)=>setNewComment(e.target.value)}
                  placeholder="서로가 존중해지는 댓글을 작성해보세요."
                />
                <button onClick={handleCommentSubmit}>등록</button>
              </>
            ) : (
              <div
                className="login-notice"
                onClick={() => setShowLoginModal(true)}
              >
                지금 로그인하고 댓글에 참여해보세요 !
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


