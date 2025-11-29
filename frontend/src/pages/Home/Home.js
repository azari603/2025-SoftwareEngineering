import BookList from "../../components/BookList/BookList";
import ReviewList from "../../components/ReviewList/ReviewList";
import Button from "../../components/Button/Button";
import SearchBar from "../../components/SearchBar/SearchBar";
import { useAuth } from "../../context/AuthContext";
import "./Home.css";
import { LayoutContext } from "../../context/LayoutContext";
import { useContext, useEffect, useState } from "react";
import * as bookAPI from "../../api/bookAPI"
import { fetchLatestFeed, fetchFollowingFeed } from "../../api/reviewAPI";

/**
 * props
 * - todaysBooks: Book[]              (비로그인: 오늘의 추천 도서)
 * - todaysReviews: Review[]          (비로그인: 오늘의 추천 서평)
 * - recommendedBooks: Book[]         (로그인: 개인화 추천 도서)
 * - followingReviews: Review[]       (로그인: 팔로잉 서평)
 */

const Home = (
) => {

  const {isLoggedIn, user}=useAuth();
  const nickname=user?.nickname||"guest";

  const [todayBooks, setTodayBooks] = useState([]);
  const [recommendedBooks, setRecommendedBooks] = useState([]);
  const [todayReviews, setTodayReviews] = useState([]);
  const [followingReviews, setFollowingReviews] = useState([]);

  const { setFooterColor } = useContext(LayoutContext);

  useEffect(() => {
    setFooterColor("#FFFFFF"); // 흰색 테마
  }, [setFooterColor]);

  useEffect(() => {
    loadHomeData();
  }, [isLoggedIn]);

  async function loadHomeData() {
    try{
      //오늘의 서평
    const latestRes = await fetchLatestFeed({ page: 0, size: 8 });
    const latestContent = Array.isArray(latestRes?.content)
        ? latestRes.content
        : [];
    setTodayReviews(latestContent);
    if (isLoggedIn) {
      //팔로잉 서평
      const followRes = await fetchFollowingFeed({ page: 0, size: 8 });
      const followContent = Array.isArray(followRes?.content)
          ? followRes.content
          : [];
      setFollowingReviews(followContent);
      // 개인화 추천 도서
      const recoRes = await bookAPI.fetchPersonalizedBooks({ page: 0, size: 10 });
      const recoBooks = Array.isArray(recoRes?.books)
          ? recoRes.books
          : [];
      setRecommendedBooks(recoBooks);
    } else {
      // 인기 도서(비로그인)
      const popularRes = await bookAPI.fetchPopularBooks({ page: 0, size: 10 });
      const popularBooks = Array.isArray(popularRes?.books)
          ? popularRes.books
          : [];
      setTodayBooks(popularBooks);
    }
    }catch (err) {
      console.error("홈 데이터 로드 중 오류:", err);
      // 에러 나면 전부 비워두기
      setTodayReviews([]);
      setFollowingReviews([]);
      setRecommendedBooks([]);
      setTodayBooks([]);
    }
    
  }
  const reviewSectionTitle=
    isLoggedIn&&followingReviews.length>0?"팔로잉 서평":"오늘의 추천 서평";
  const reviewForSection=isLoggedIn&&followingReviews.length>0?followingReviews:todayReviews;
  const booksForSection=isLoggedIn?recommendedBooks:todayBooks;

  return (
    <div className="home">
    
      <div className="home-main">
        <section className="main-hero">
          <div className="main-hero__inner">
            <h1 className="main-hero__title">
              당신의 서재와,<br />모두의 서평이 만나는 곳
            </h1>

            <div className="main-hero__actions">
              <Button variant="squareOutline" size="medium" to="/feed">서평 둘러보기</Button>
              {isLoggedIn?(
                <>
                  <Button variant="filled" size="medium" to="/write/book">책 기록하기</Button>
                </>
              ):(
                <>
                  <Button variant="filled" size="medium" to="/login">책 기록하기</Button>
                </>
              )}
              
            </div>

            <div className="main-hero__search">
              <SearchBar variant="filled" placeholder="책 제목 또는 저자명을 검색해 보세요" />
            </div>
          </div>
        </section>

        {/*서평 섹션 */}
        <section className="home-section">
          <div className="review wrapper">
            <h2 className="section-title">{reviewSectionTitle}</h2>
              <ReviewList reviews={reviewForSection} mode="carousel" visibleCount={4} />
          </div>
        </section>

        {/*도서 섹션 */}
        <section className="home-section">
          <div className="book wrapper">
            <h2 className="section-title">
              {isLoggedIn?(
                <>
                  <span className="section-title__nickname">{nickname}</span>님을 위한 추천 도서
                </>
              ):(
                "오늘의 추천 도서"
              )}
            </h2>
            <BookList books={booksForSection} mode="carousel" visibleCount={6} cardSize="lg" />
          </div>
        </section>
      </div>
    </div>
  );
};

export default Home;
