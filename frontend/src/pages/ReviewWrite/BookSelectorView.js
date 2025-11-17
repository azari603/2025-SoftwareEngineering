import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { searchBooks, getBooksByStatus } from "../../api/bookAPI";
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

    const [query, setQuery]=useState("");
    const [tab, setTab]=useState("reading");
    const [books, setBooks]=useState([]);
    const [loading, setLoading]=useState(false);

    useEffect(()=>{
        if(!query.trim()) loadMyLibrary(tab);
    },[tab]);

    //검색
    const handleSearch=async(e)=>{
        e.preventDefault();
        if(query.trim()){
            setLoading(true);
            const res=await searchBooks({query});
            if (res.success) setBooks(res.books);
            setLoading(false);
        }else{
            loadMyLibrary(tab);
        }
    };
    // 내 서재 불러오기
    const loadMyLibrary = async (tab) => {
        setLoading(true);
        const status=mapStatus(tab)
        const res = await getBooksByStatus(status);
        if (res.success) setBooks(res.books);
        setLoading(false);
    };

    const handleSelectBook = (book) => {
        navigate(`/write/review?bookId=${book.isbn}`);
    };

    const isSearching=query.trim().length>0;

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
            읽고 있어요
            </button>
            <button
            className={tab === "finished" ? "active" : ""}
            onClick={() => setTab("finished")}
            >
            다 읽었어요
            </button>
            <button
            className={tab === "want" ? "active" : ""}
            onClick={() => setTab("want")}
            >
            읽고 싶어요
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
                key={book.isbn}
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