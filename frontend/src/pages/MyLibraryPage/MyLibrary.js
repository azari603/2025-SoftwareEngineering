import React, { useState, useEffect,useContext } from "react";
import "./MyLibrary.css";
import { getMyLibraryBooks } from "../../api/MyLibraryApi";
import BookCard from "../../components/BookCard/BookCard";
import { LayoutContext } from "../../context/LayoutContext";
import { useNavigate, useLocation } from "react-router-dom";
import { useBookStatus } from "../../context/BookStatusContext";


export default function MyLibraryPage() {
  const [activeTab, setActiveTab] = useState("reading");
  const [books, setBooks] = useState([]);
  const [loading, setLoading] = useState(true);
  const { setFooterColor } = useContext(LayoutContext);
  const navigate = useNavigate();
  const location= useLocation();
  const { getBooksByStatus } = useBookStatus();

  const tabs = [
    { id: "reading", label: "읽고있는 책" },
    { id: "want", label: "읽고싶은 책" },
    { id: "finished", label: "다 읽은 책" },
  ];

  useEffect(() => {
    const loadBooks = async () => {
      setLoading(true);
      const res = await getMyLibraryBooks(activeTab);
      if (!res.success) return;
      //책 여러권일때 테스트용
      //  if (res.success) {
      //    const manyBooks = Array(50).fill(res.books[0]);
      //    setBooks(manyBooks);
      // }

      const isbns = getBooksByStatus(activeTab);
      const filtered = res.books.filter((b) => isbns.includes(b.isbn));
      setBooks(filtered);
      setLoading(false);
    };
    loadBooks();
  }, [activeTab,getBooksByStatus]);

  useEffect(() => {
    setFooterColor("#FDFBF4"); // 흰색 테마
  }, [setFooterColor]);

  return (
    <div className="library-container">
      <aside className="library-sidebar">
        <button
          className={`library-sidebar-btn ${
            location.pathname.includes("/profile/library") ? "active" : ""
          }`}
          onClick={() => navigate("/profile/library")}
        >
          나의 서재
        </button>

        <button
          className={`library-sidebar-btn ${
            location.pathname.includes("/profile/reviews") ? "active" : ""
          }`}
          onClick={() => navigate("/profile/reviews")}
        >
          나의 서평
        </button>

        <button
          className={`library-sidebar-btn ${
            location.pathname.includes("/profile/stats") ? "active" : ""
          }`}
          onClick={() => navigate("/profile/stats")}
        >
          독서 통계
        </button>
      </aside>

      <main className="library-main">
        <div className="library-header">
          <h2>
            나의 기록  &gt; <span className="breadcrumb"> 나의 서재</span>
          </h2>
        </div>

        <div className="tab-wrapper">
          <div className="tab-header">
            <div className="tab-buttons">
              {tabs.map((tab) => (
                <button
                  key={tab.id}
                  className={`tab-btn ${
                    activeTab === tab.id ? "active" : ""
                  }`}
                  onClick={() => setActiveTab(tab.id)}
                >
                  {tab.label}
                </button>
              ))}
            </div>
            <span className="book-count">({books.length}권)</span>
          </div>

          {loading ? (
            <p className="loading-text">로딩중...</p>
          ) : (
            <div className="book-grid">
              {books.map((book, index) => (
                <BookCard key={index} book={book} size="md" />
              ))}
            </div>
          )}
        </div>
      </main>
    </div>
  );
}
