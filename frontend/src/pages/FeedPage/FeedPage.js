import { useContext, useEffect, useState } from "react";
import "./FeedPage.css";
import FeedReviewCard from "../../components/FeedReviewCard/FeedReviewCard";
import { useAuth } from "../../context/AuthContext";
import { LayoutContext } from "../../context/LayoutContext";
import { fetchFollowingFeed, fetchLatestFeed } from "../../api/reviewAPI";

export default function FeedPage() {
  const [activeTab, setActiveTab] = useState("latest");
  const [reviews, setReviews] = useState([]);
  const { isLoggedIn } = useAuth();
  const { setFooterColor } = useContext(LayoutContext);

  
  useEffect(() => {
    loadFeed();
  }, [activeTab]);


  useEffect(() => {
    setFooterColor("#FDFBF4");
  }, []);

    //피드 로딩
  const loadFeed = async () => {
    let res;

    if (activeTab === "latest") {
      res = await fetchLatestFeed({ page: 0, size: 8 });
      setReviews(res.content ?? []);
    } else if (activeTab === "following") {
      res = await fetchFollowingFeed({ page: 0, size: 8 });

      if (res.content) {
        setReviews(res.content);
      } else {
        setReviews([]);
      }
    }
  };

  /** 팔로잉 버튼 클릭 시 */
  const handleFollowingClick = () => {
    if (!isLoggedIn) {
      alert("로그인이 필요한 기능입니다.");
      return;
    }
    setActiveTab("following");
  };

  return (
    <div className="feed-list-view">

      <div className="feed-list-card">

        {/* 탭 */}
        <div className="feed-tabs-row">
          <button
            className={`feed-tab ${activeTab === "latest" ? "active" : ""}`}
            onClick={() => setActiveTab("latest")}
          >
            최신 서평
          </button>

          <button
            className={`feed-tab ${activeTab === "following" ? "active" : ""}`}
            onClick={handleFollowingClick}
          >
            팔로잉 서평
          </button>
        </div>

        {/* 리뷰 리스트 */}
        <div className="feed-list">
          {reviews.map((review) => (
            <FeedReviewCard key={review.reviewId} review={review} />
          ))}
        </div>

      </div>
    </div>
  );
}