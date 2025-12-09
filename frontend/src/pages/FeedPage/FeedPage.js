import { useContext, useEffect, useRef, useState } from "react";
import "./FeedPage.css";
import FeedReviewCard from "../../components/FeedReviewCard/FeedReviewCard";
import { useAuth } from "../../context/AuthContext";
import { LayoutContext } from "../../context/LayoutContext";
import { fetchFollowingFeed, fetchLatestFeed } from "../../api/reviewAPI";
import feed_img from "../../assets/feed_img.png"
import ReviewCard from "../../components/ReviewCard/ReviewCard";
export default function FeedPage() {
  const [activeTab, setActiveTab] = useState("latest");
  const [reviews, setReviews] = useState([]);
  const { isLoggedIn } = useAuth();
  const { setFooterColor } = useContext(LayoutContext);
  const [hasMore, setHasMore] =useState(true);
  const [page, setPage]=useState(0);
  const observerRef=useRef(null);
  
  useEffect(() => {
    setFooterColor("#FDFBF4");
  }, []);

   /** 탭 변경 시 초기화 + 첫 페이지 로드 */
  useEffect(() => {
    setReviews([]);
    setPage(0);
    setHasMore(true);

    loadFeed(0); 
  }, [activeTab]);


  /** page가 증가할 때 추가 로딩 */
  useEffect(() => {
    if (page === 0) return; 
    if (!hasMore) return;

    loadMore(page);
  }, [page]);


  /** 초기 로딩: page=0 */
  const loadFeed = async () => {
    let res;

    if (activeTab === "latest") {
      res = await fetchLatestFeed({ page: 0, size: 9 });
    } else {
      if (!isLoggedIn) return;
      res = await fetchFollowingFeed({ page: 0, size: 9 });
    }

    if (!res || !res.content) {
      setReviews([]);
      return;
    }
    

    setReviews(res.content);
    if (res.last) setHasMore(false);
  };


  /** 추가 로딩: page >= 1 */
  const loadMore = async (pageNum) => {
    let res;

    if (activeTab === "latest") {
      res = await fetchLatestFeed({ page: pageNum, size: 9 });
    } else {
      res = await fetchFollowingFeed({ page: pageNum, size: 9 });
    }

    if (!res || !res.content) return;

    if (res.content.length === 0) {
    setHasMore(false);
    return;
    }

    setReviews((prev) => [...prev, ...res.content]);

    if (res.last) setHasMore(false);
  };


  /** IntersectionObserver (무한스크롤) */
  useEffect(() => {
    if (!hasMore) return;

    const observer = new IntersectionObserver(
      (entries) => {
        if (entries[0].isIntersecting) {
          setPage((prev) => prev + 1);
        }
      },
      { threshold: 1 }
    );

    if (observerRef.current) observer.observe(observerRef.current);

    return () => observer.disconnect();
  }, [hasMore]);

  /** 팔로잉 버튼 클릭 시 */
  const handleFollowingClick = () => {
    if (!isLoggedIn) {
      alert("로그인이 필요한 기능입니다.");
      return;
    }
    setActiveTab("following");
  };

  return (
    <div className="feed-page-wrapper">
      {/*hero*/}
      <section className="feed-hero">
        <div className="feed-hero-text fade-in-up">
          <h1 className="feed-hero-title">둘러보기</h1>
          <p className="feed-hero-desc">
            다른 독자들의 시선에서 만나는 또 다른 이야기,<br/>
            지금 사람들은 어떤 책을 읽고 있을까요?
          </p>
        </div>
      </section>

      <div className="feed-tabs-row fade-in-up">
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

      <div className="feed-grid fade-smooth">
        {reviews.map((review) => (
          <FeedReviewCard key={review.reviewId} review={review}/>
        ))}
      </div>
      {hasMore && <div ref={observerRef} className="feed-sentinel" />}
    </div>
  );
}