import { useNavigate, useParams } from "react-router-dom";
import "./ProfilePage.css";
import profile_img from "../../assets/profile_img.png";
import ReviewList from "../../components/ReviewList/ReviewList";
import no_result from "../../assets/no_result.png";
import BookList from "../../components/BookList/BookList";
import settings_btn from "../../assets/option.png";
import { useContext, useEffect, useState } from "react";
import { useAuth } from "../../context/AuthContext"; 
import { LayoutContext } from "../../context/LayoutContext";
import { getProfile, getMyProfile, getMyStarredBooks } from "../../api/authApi";
import { FaStar } from "react-icons/fa";

const base=process.env.REACT_APP_BASE_URL;
function fullUrl(path) {
  if (!path) return null;
  if (path.startsWith("http")) return path;
  return `${base}/${path}`; // base 붙이기
}

export default function ProfilePage() {
  const { user } = useAuth();
  const [selectedRating, setSelectedRating] = useState(5);
  const navigate = useNavigate();
  const { setFooterColor } = useContext(LayoutContext);
  const [profile, setProfile]=useState(null);
  const [loading, setLoading]=useState(true);
  const {username: paramUsername}=useParams();
  const targetUsername=paramUsername??user?.username; //조회할 username 결정
  const [starredBooks, setStarredBooks]=useState([]) //별점별 책목록


  useEffect(() => {
    setFooterColor("#FDFBF4"); // 흰색 테마
  }, []);

  //프로필 조회 호출
  useEffect(()=>{
    if(!targetUsername) return;
    async function loadProfile(){
      const res=await getProfile(targetUsername,{include:["reviews","stars"]})
      setProfile(res.profile);
      setLoading(false);
    }
    loadProfile();
  },[user]);

  useEffect(()=>{
    async function loadStarredBooks(){
      const res=await getMyStarredBooks(selectedRating,0,20);
      const mapped=res.content.map((b)=>({
        id:b.bookId,
        name:b.name,
        author:b.author,
        image:b.imageUrl,
        avgStar:b.avgStar??0,
        reviewCount:b.reviewCount??0,
        rating:selectedRating,
      }));
      setStarredBooks(mapped);
    }
    loadStarredBooks();
  },[selectedRating]);

  const filteredBooks = starredBooks;
  if(loading){
    return <div className="profile-container">로딩 중...</div>
  }
  if (!profile) {
    return <div className="profile-container">프로필 정보를 불러올 수 없습니다.</div>;
  }

  return (
    <div className="profile-container">
      <div className="profile-card">
        <div className="profile-header-bg" style={{
            backgroundImage: profile.backgroundImageUrl
              ? `url(${fullUrl(profile.backgroundImageUrl)})`
              : "none",
            backgroundSize: "cover",
            backgroundPosition: "center",
          }}
        >
          {(!paramUsername||paramUsername===user?.username)&&(
              <button
              className="settings-btn"
              onClick={() => navigate("/profile/settings")}
            >
              <img src={settings_btn} alt="설정" />
            </button>
          )}
          
        </div>
        <div className="profile-info">
          <div className="profile-img">
            <img src={fullUrl(profile.profileImageUrl)||profile_img} 
            
            alt="프로필 이미지" />
          </div>
          <h2 className="username">{profile.nickname}</h2>
          <p className="userid">@{profile.username}</p>
          <p className="intro">
            {profile.intro && profile.intro.trim() !== ""
              ? profile.intro
              : "나를 소개할 수 있는 한 문장을 적어보세요"}
          </p>
          <div className="follow-info">
            <span>
              팔로잉 <b>{profile.followingCount}</b>
            </span>
            <span>
              팔로워 <b>{profile.followerCount}</b>
            </span>
            <span>
              읽은책 <b>{profile.completedBookCount}</b>
            </span>
          </div>
        </div>

        {/* 별점 섹션 */}
        <div className="book-section">
          <h3>
            <span className="user-id">{profile.nickname}</span>님의 별점 목록
          </h3>

          <div className="rating-tabs">
            {[5, 4, 3, 2, 1].map((r) => (
              <button
                key={r}
                className={`tab ${selectedRating === r ? "active" : ""}`}
                onClick={() => setSelectedRating(r)}
              >
                 <FaStar className="star-icon"/>{r}
              </button>
            ))}
          </div>

          <div className="profile-book-list">
            {filteredBooks.length > 0 ? (
              <BookList
                books={filteredBooks}
                mode="carousel"
                visibleCount={5}
                cardSize="lg"
              />
            ) : (
              <div className="no-books">
                <img
                  src={no_result}
                  alt="책 없음"
                  className="no-books-image"
                />
                <p>{selectedRating}점을 준 책은 없습니다.</p>
              </div>
            )}
          </div>
        </div>

        {/* 리뷰 섹션 */}
        <div className="review-section">
          <h3 className="review-section-title">공개된 서평</h3>
          <div className="review-list">
            <ReviewList
              reviews={profile.reviews.content}
              mode="carousel"
              visibleCount={4}
            />
          </div>
        </div>
      </div>
    </div>
  );
}
