import { useLocation } from "react-router-dom";
import { useEffect, useState } from "react";
import { dummyBooks } from "../../mocks/dummyBooks";
import BookList from "../../components/BookList/BookList";
import bookImg from "../../assets/search_book.png"
import "./SearchPage.css"

export default function SearchResult() {
  const { search } = useLocation(); // (1) URL 정보 가져오기
  const query = new URLSearchParams(search).get("query") || ""; // (2) 검색어 추출
  const [results, setResults] = useState([]); // (3) 결과 저장용

  // (4) 검색어(query)가 바뀔 때마다 실행
  useEffect(() => {
    if (query) {
      const filtered = dummyBooks.filter(
        (book) =>
          book.title.includes(query) || book.author.includes(query)
      );
      setResults(filtered);
    }
  }, [query]);

  return (
    <div className="search-result-page">
      <div className="search-header">
        <h2>
          <span className="highlight">‘{query}’</span> 에 대한 검색 결과
        </h2>
      </div>

      
        {results.length>0?(
            <div className="search-result-container">
                <BookList books={results} mode="list" cardSize="lg" />
            </div>
    ):(
        <div className="non-result">
            <img src={bookImg} alt="book-img" className="book-img"/>
            <p>검색 결과가 없습니다</p>
        </div>
    )}
        
    </div>
  );
}
