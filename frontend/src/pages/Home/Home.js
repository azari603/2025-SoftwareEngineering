import Header from "../../components/Header/Header";
import BookList from "../../components/BookList/BookList";
import ReviewList from "../../components/ReviewList/ReviewList";
import Button from "../../components/Button/Button";
import SearchBar from "../../components/SearchBar/SearchBar";
import "./Home.css";

/**
 * props
 * - isLoggedIn: boolean
 * - nickname: string
 * - todaysBooks: Book[]              (비로그인: 오늘의 추천 도서)
 * - todaysReviews: Review[]          (비로그인: 오늘의 추천 서평)
 * - recommendedBooks: Book[]         (로그인: 개인화 추천 도서)
 * - followingReviews: Review[]       (로그인: 팔로잉 서평)
 */

const Home = ({ 
  isLoggedIn = false, 
  nickname="guest",
  todayBooks=[],
  todayReviews=[],
  recommendedBooks=[],
  followingReviews=[],
}) => {

  const reviewSectionTitle=isLoggedIn?"팔로잉 서평":"오늘의 추천 서평";
  const reviewForSection=isLoggedIn?followingReviews:todayReviews;
  const booksForSection=isLoggedIn?recommendedBooks:todayBooks;

  return (
    <div className="home">
      <Header isLoggedIn={isLoggedIn}/>

      <main className="home-main">
        <section className="main-hero">
          <div className="main-hero__inner">
            <h1 className="main-hero__title">
              당신의 서재와,<br />모두의 서평이 만나는 곳
            </h1>

            <div className="main-hero__actions">
              {isLoggedIn?(
                <>
                  <Button variant="squareOutline" size="medium" to="/feed">서평 둘러보기</Button>
                  <Button variant="filled" size="medium" to="/write">책 기록하기</Button>
                </>
              ):(
                <>
                  <Button variant="squareOutline" size="medium" to="/login">서평 둘러보기</Button>
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
            <BookList books={booksForSection} mode="carousel" visibleCount={6} cardSize="sm" />
          </div>
        </section>
      </main>
    </div>
  );
};

export default Home;
