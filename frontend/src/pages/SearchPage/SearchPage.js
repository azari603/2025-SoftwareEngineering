import { useLocation } from "react-router-dom";
import { useContext, useEffect, useRef, useState } from "react";
import { dummyBooks } from "../../mocks/dummyBooks";
import BookList from "../../components/BookList/BookList";
import bookImg from "../../assets/search_book.png"
import Pagination from "../../components/Pagination/Pagination"
import {searchBooks} from "../../api/bookAPI";
import { LayoutContext } from "../../context/LayoutContext";
import "./SearchPage.css"

export default function SearchResult() {
  const { search } = useLocation(); // (1) URL 정보 가져오기
  const query = new URLSearchParams(search).get("query") || ""; // (2) 검색어 추출
  const [results, setResults] = useState([]); // (3) 결과 저장용

  const [page, setPage]=useState(1); //현재 페이지 번호
  const pageSize=10; //페이지당 책 개수
  const [totalCount, setTotalCount]=useState(0);//총 검색 결과 수
  const { setFooterColor } = useContext(LayoutContext);
  const prevQuery=useRef(query); //이전 쿼리 저장
  const lastFetchRef=useRef({query:null, page:null});

  useEffect(() => {
    setFooterColor("#FDFBF4");
  }, [setFooterColor]);

  useEffect(()=>{
    if(!query) return;
    let effectivePage=page;
    // 쿼리가 변경되었을 때
    if (prevQuery.current !== query) {
      prevQuery.current = query; // 이전 쿼리 갱신
      effectivePage=1;
      if(page!==1){
        setPage(1);
      }
    }
    if(lastFetchRef.current.query===query&&lastFetchRef.current.page===effectivePage){
      return;
    }
    lastFetchRef.current={query, page: effectivePage};
    const fetchData = async () => {
    const res = await searchBooks({
      q: query,
      page: effectivePage,
      size: pageSize,
    });

      setResults(res?.books ?? []);
      setTotalCount(res?.totalCount ?? 0);
    };

    // fetch 실행
    fetchData();
    
  },[query, page]);

  return (
    <div className="search-result-page">
      <div className="search-header">
        <h2>
          <span className="highlight">‘{query}’</span> 에 대한 검색 결과
        </h2>
      </div>

      
        {results.length>0?(
          <>
          <div className="search-result-container">
                <BookList books={results} mode="list" cardSize="lg" />
            </div>

              <Pagination
              currentPage={page}
              totalCount={totalCount}
              pageSize={pageSize}
              onPageChange={(newPage)=>setPage(newPage)}/>
            
          </>
            
    ):(
        <div className="non-result">
            <img src={bookImg} alt="book-img" className="book-img"/>
            <p>검색 결과가 없습니다</p>
        </div>
    )}
        
    </div>
  );
}
