import React, { useState, useEffect,useContext } from "react";
import "./MyLibrary.css";
import BookCard from "../../components/BookCard/BookCard";
import { LayoutContext } from "../../context/LayoutContext";
import { useNavigate, useLocation } from "react-router-dom";
import { getBooksByStatus } from "../../api/bookAPI";

function mapStatus(tab){
  switch(tab){
    case "reading":
      return "READING"
    case "finished":
      return "COMPLETED"
    case "want":
      return "WISHILIST"
    default:
      return "READING"
  }
}

export default function MyLibraryPage() {
  const [activeTab, setActiveTab] = useState("reading");
  const [books, setBooks] = useState([]);
  const [loading, setLoading] = useState(true);
  const { setFooterColor } = useContext(LayoutContext);
  const navigate = useNavigate();
  const location= useLocation();

  const tabs = [
    { id: "reading", label: "읽고있는 책" },
    { id: "want", label: "읽고싶은 책" },
    { id: "finished", label: "다 읽은 책" },
  ];

  useEffect(() => {
    const loadBooks = async () => {
      setLoading(true);
      const status=mapStatus(activeTab);
      const res = await getBooksByStatus(status);
      if (res.success)
        setBooks(res.books);
      setLoading(false);
    };
    loadBooks();
  }, [activeTab]);

  useEffect(() => {
    setFooterColor("#FDFBF4"); // 흰색 테마
  }, [setFooterColor]);

  return (
    <div className="library-container">

      <main className="library-main">
        <div className="library-header">
          <h2>
            나의 기록  &gt; <span className="breadcrumb"> 나의 서재</span>
          </h2>
        </div>

        <div className="tab-wrapper">
          <div className="tab-header-library">
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
