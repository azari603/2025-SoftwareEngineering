import { useEffect, useContext, useState } from "react";
import "./MyReviewPage.css";
import ReviewItem from "../../components/BookReviewList/BookReviewList";
import { LayoutContext } from "../../context/LayoutContext";
import { useAuth } from "../../context/AuthContext";
import { getMyReviews, getLikedReviews } from "../../api/reviewAPI";

export default function MyReviewPage() {
  const { setFooterColor } = useContext(LayoutContext);
  const [activeTab, setActiveTab] = useState("my"); 
  const [reviews, setReviews] = useState([]);
  const {user}=useAuth();
  const [likedTrigger, setLikedTrigger] = useState(0);
  
  useEffect(() => {
    setFooterColor("#FDFBF4");
  }, [setFooterColor]);

  useEffect(() => {
  if (!user) return; 
    async function load() {
      if (activeTab === "my") {
        const data = await getMyReviews({
        page: 0,
        size: 20,
        visibility: "ALL",
        status: "PUBLISHED",
        sort: "createdAt,desc",
        }
        );
        setReviews(data.content);
      } else {
        const data = await getLikedReviews({
          page: 0,
          size: 10,
          sort: "likedAt,desc"
        });

        if (data.ok) {
          setReviews(data.content);
        } else {
          setReviews([]); 
        }
      }
    }
    load();
  }, [activeTab,user,likedTrigger]);

  return (
    <div className="myReview-container">
      <main className="myReview-main">
        <div className="myReview-header">
          <h2>
            나의 기록 &gt; <span className="myReview-breadcrumb">나의 서평</span>
          </h2>
        </div>

        <div className="myReview-wrapper">
          <div className="myReview-tab-header">
            <div className="myReview-tab-buttons">
              <button
                className={`myReview-tab-btn ${
                  activeTab === "my" ? "active" : ""
                }`}
                onClick={() => setActiveTab("my")}
              >
                나의 서평 목록
              </button>

              <button
                className={`myReview-tab-btn ${
                  activeTab === "liked" ? "active" : ""
                }`}
                onClick={() => setActiveTab("liked")}
              >
                좋아요 한 서평
              </button>
            </div>

            <span className="myReview-count">({reviews.length}개)</span>
          </div>

          <div className="myReview-list">
            {reviews.length === 0 ? (
              <p className="myReview-empty">서평이 없습니다.</p>
            ) : (
              reviews.map((review) => (
                <ReviewItem key={review.reviewId} review={review} setLikedTrigger={setLikedTrigger} currentUser={user}/>
              ))
            )}
          </div>
        </div>
      </main>
    </div>
  );
}
