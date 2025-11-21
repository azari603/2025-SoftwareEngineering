import { use, useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { searchBooks, getBooksByStatus } from "../../api/bookAPI";
import { useAuth } from "../../context/AuthContext";
import "./BookSelectorView.css"

function mapStatus(tab){
    switch(tab){
        case "reading":
            return "READING"
        case "finished":
            return "COMPLETED"
        case "want":
            return "WISHLIST";
        default:
            return null
    }
}

export default function BookSelectorView(){
    const navigate=useNavigate();
    const {user, isLoggedIn}=useAuth();

    const [query, setQuery]=useState("");
    const [tab, setTab]=useState("reading");
    const [books, setBooks]=useState([]);
    const [loading, setLoading]=useState(false);
    const isSearching = query.trim().length > 0;

    //탭 바뀌면 내 서재 다시 불러오기 (검색중 아닐때)
    useEffect(() => {
    if (!isSearching) {
      loadMyLibrary(tab);
    }
  }, [tab]);

  //검색어 지워진 경우 내서재 다시 불러오기
    useEffect(() => {
        if (!isSearching) {
        loadMyLibrary(tab);
        }
    }, [query]);

    
    //검색
    const handleSearch=async(e)=>{
        e.preventDefault();
        if(query.trim()){
            setLoading(true);
            const res=await searchBooks({query});
            setBooks(res.books);
            setLoading(false);
        }else{
            loadMyLibrary(tab);
        }
    };
    // 내 서재 불러오기
    const loadMyLibrary = async (tab) => {
        if (!isLoggedIn || !user) return;
        setLoading(true);
        const status=mapStatus(tab)
        const res = await getBooksByStatus({status, username: user.username});
        setBooks(res.books);
        setLoading(false);
    };

    const handleSelectBook = (book) => {
        navigate(`/write/review?bookId=${book.bookId}`);
    };

    

    return (
        <div className="book-selector-view">
        <div className="book-selector-card">
            <div className="top-controls">
            <h2 className="page-title">서평 작성할 도서 찾기</h2>

            <form className="search-box" onSubmit={handleSearch}>
            <input
                type="text"
                placeholder="책 제목 또는 저자명을 검색해 보세요"
                value={query}
                onChange={(e) => setQuery(e.target.value)}
            />
            </form>
        </div>

        {/* 탭 */}
        {!isSearching&&(
            <div className="book-tabs">
            <button
            className={tab === "reading" ? "active" : ""}
            onClick={() => setTab("reading")}
            >
            읽고있는 책
            </button>
            <button
            className={tab === "want" ? "active" : ""}
            onClick={() => setTab("want")}
            >
            읽고싶은 책
            </button>
            <button
            className={tab === "finished" ? "active" : ""}
            onClick={() => setTab("finished")}
            >
            다 읽은 책
            </button>
            </div>

        )}
        
        {/* 로딩 표시 */}
        {loading && <p className="loading-text">불러오는 중...</p>}

        {/* 리스트 */}
        <div className="book-list">
            {books.length > 0 ? (
            books.map((book) => (
                <div
                key={book.bookId}
                className="book-item"
                onClick={() => handleSelectBook(book)}
                >
                <div className="book-thumbnail">
                    <img src={book.image} alt={book.title} />
                </div>
                <div className="book-info">
                    <p className="book-title">{book.title}</p>
                    <p className="book-author">{book.author}</p>
                    <p className="book-publisher">{book.publisher || "출판사 정보 없음"}</p>
                </div>
                </div>
            ))
            ) : (
            !loading && <p className="no-result">표시할 책이 없습니다.</p>
            )}
        </div>
        </div>
        
        </div>
    );

}