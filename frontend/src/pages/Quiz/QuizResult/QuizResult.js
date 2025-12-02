import React, { useEffect, useState } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import bookbtiApi from "../../../api/bookbtiAPI";
import BookList from "../../../components/BookList/BookList";
import "./QuizResult.css";
import {useAuth} from "../../../context/AuthContext";

export default function QuizResult() {
  const {user} =useAuth();
  const navigate = useNavigate();
  const location = useLocation();

  const result = location.state?.result;
  const resultId=location.state?.resultId;

  // 추천도서, 로딩 state
  const [recommendations, setRecommendations] = useState([]);
  const [imageLoaded, setImageLoaded] = useState(false);
  const resultImageUrl = result ? `/results/${result.code}.png` : "";
  console.log("result.code =", result.code); 


useEffect(() => {
  const loadRecommendations = async () => {
    try {
      const res = await bookbtiApi.getRecommendations(resultId);
      const mapped = (res.data.content || []).map(item => ({
        id: item.bookId,
        name:item.name,
        author: item.author,
        image: item.imageUrl,
        thumbnail: item.imageUrl,
      }));

      console.log(" 매핑된 추천 결과:", mapped);

      setRecommendations(mapped);
    } catch (err) {
      console.error("추천 도서 로딩 실패:", err);
    }
  };

  loadRecommendations();
}, [resultId]);


  if (!result) {
    return (
      <div className="quizresult">
        <h2>결과 정보를 찾을 수 없습니다.</h2>
        <button onClick={() => navigate("/")}>홈으로 돌아가기</button>
      </div>
    );
  }

  if (!imageLoaded) {
    return (
      <div className="quizresult">
        <main className="quizresult-main">
          <div className="quizresult-card">
            <h2 className="quizresult-title">로딩 중...</h2>
              {/* 결과 이미지 표시 */}
              <img
                src={resultImageUrl}
                alt=""
                style={{ display: "none" }}
                onLoad={() => setImageLoaded(true)}
              />
            </div>
        </main>
      </div>
    );
  }

  return (
    <div className="quizresult">
      <main className="quizresult-main">
        <div className="quizresult-card">
          <h2 className="quizresult-title">
            <span className="nickname">{user?.nickname}</span>님의 책BTI는 ...
          </h2>

          {/* 타입 출력 */}
          <h3 className="quizresult-subtitle">{result.code}</h3>
          <img
              src={resultImageUrl}
              alt={`${result.code} 타입 이미지`}
              className="quizresult-image"
            />

          {/* 설명 */}
          <div className="quizresult-desc">
            {result.description
              .split("\n")
              .filter(line => line.trim() !== "")
              .map((line, index) => {
                
                // 제목 라인 판별
                const isTitleLine =
                  line.trim().startsWith("✨") ||
                  line.trim().startsWith("🌿") ||
                  line.trim().startsWith("🌙") ||
                  line.trim().startsWith("💛");

                // 특정 단어만 강조하는 함수
                const highlightWord = (text, word) => {
                  // 단어 기준으로 split
                  const parts = text.split(word);
                  return parts.map((part, idx) => (
                    <React.Fragment key={idx}>
                      {part}
                      {idx < parts.length - 1 && (
                        <span className="highlight">{word}</span>
                      )}
                    </React.Fragment>
                  ));
                };

                return (
                  <p
                    key={index}
                    className={isTitleLine ? "desc-title-line" : ""}
                  >
                    {highlightWord(line, result.label)}
                  </p>
                );
              })}
          </div>



          <hr className="quizresult-divider" />

          <h4 className="quizresult-recommend-title">
            당신에게 어울리는 책을 <span>AI</span>가 추천해보았어요!
          </h4>

          <div className="quizresult-books">
            <BookList
              books={recommendations}
              mode="carousel"
              visibleCount={5}
              cardSize="md"
            />
          </div>
        </div>
      </main>
    </div>
  );
}
