import { useNavigate } from "react-router-dom";
import "./ProfilePage.css";
import profile_img from "../../assets/profile_img.png";
import { dummyBooks } from "../../mocks/dummyBooks";
import { makeDummyReviews } from "../../mocks/dummyReviews";
import ReviewList from "../../components/ReviewList/ReviewList";
import no_result from "../../assets/no_result.png";
import BookList from "../../components/BookList/BookList";
import settings_btn from "../../assets/option.png";
import { useContext, useEffect, useState } from "react";
import { useAuth } from "../../context/AuthContext"; 
import { LayoutContext } from "../../context/LayoutContext";
import { getMyProfile } from "../../api/authApi";

export default function ProfilePage() {
  const { user } = useAuth();
  const [selectedRating, setSelectedRating] = useState(5);
  const navigate = useNavigate();
  const { setFooterColor } = useContext(LayoutContext);
  const [profile, setProfile]=useState(null);
  const [loading, setLoading]=useState(true);

  useEffect(() => {
    setFooterColor("#FDFBF4"); // 흰색 테마
  }, []);

  //프로필 조회 호출
  useEffect(()=>{
    async function loadProfile(){
      const res=await getMyProfile({include: ["reviews", "stars"]})
      if (res.success){
        setProfile(res.profile);
      }
      setLoading(false);
    }
    loadProfile();
  },[]);

  if(loading){
    return <div className="profile-container">로딩 중...</div>
  }
  if (!profile) {
    return <div className="profile-container">프로필 정보를 불러올 수 없습니다.</div>;
  }

  const filteredBooks = dummyBooks.filter(
    (book) => book.rating === selectedRating
  );

  return (
    <div className="profile-container">
      <div className="profile-card">
        <div className="profile-header-bg" style={{
            backgroundImage: profile.backgroundImageUrl
              ? `url(${profile.backgroundImageUrl})`
              : "none",
            backgroundSize: "cover",
            backgroundPosition: "center",
          }}
        >
          <button
            className="settings-btn"
            onClick={() => navigate("/profile/settings")}
          >
            <img src={settings_btn} alt="설정" />
          </button>
        </div>
        <div className="profile-info">
          <div className="profile-img">
            <img src={profile.profileImageUrl || profile_img} alt="프로필 이미지" />
          </div>
          <h2 className="username">{profile.nickname}</h2>
          <p className="userid">@{profile.username}</p>

          <div className="follow-info">
            <span>
              팔로잉 <b>{profile.followingsCount}</b>
            </span>
            <span>
              팔로워 <b>{profile.followersCount}</b>
            </span>
            <span>
              읽은책 <b>{profile.readBooksCount}</b>
            </span>
          </div>

          <p className="intro">
            {profile.intro && profile.intro.trim() !== ""
              ? profile.intro
              : "나를 소개할 수 있는 한 문장을 적어보세요"}
          </p>

          <div className="goal-section">
            <div className="goal-header">
              <h4>이달의 목표</h4>
              <p className="goal-count">
                <span className="goal-current">8</span>
                <span className="goal-total">/10권</span>
              </p>
            </div>
            <div className="goal-progress">
              <div className="goal-fill" style={{ width: "80%" }}></div>
            </div>
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
                ⭐ {r}
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
          <h3>공개된 서평</h3>
          <div className="review-list">
            <ReviewList
              reviews={makeDummyReviews(8, { withBook: true })}
              mode="carousel"
              visibleCount={3}
            />
          </div>
        </div>
      </div>
    </div>
  );
}
