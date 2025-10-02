import { useLocation } from "react-router-dom";
import Header from "../../../components/Header/Header";
import quizResults from "../../../mocks/dummyResults";
import "./QuizResult.css";
import BookList from "../../../components/BookList/BookList"; 
import { dummyBooks } from "../../../mocks/dummyBooks";

export default function QuizResult({ isLoggedIn = false }) {
  const location = useLocation();
  const scores = location.state?.scores || {};
  const resultKey = location.state?.resultKey || "future";
  const result = quizResults[resultKey];

  // 점수 합계
  const totalScore = Object.values(scores).reduce((sum, v) => sum + v, 0);

  // 퍼센트 계산
  const percentages = totalScore > 0 
    ? Object.fromEntries(
        Object.entries(scores).map(([category, score]) => [
          category,
          ((score / totalScore) * 100).toFixed(1),
        ])
      )
    : {};

  // 카테고리 라벨
  const categoryLabels = {
    romance: "로맨스",
    history: "역사",
    fantasy: "판타지",
    thriller: "스릴러",
    science: "과학",
    future: "미래지향",
  };

  // 점수 내림차순 정렬
  const sortedScores = Object.entries(scores).sort((a, b) => b[1] - a[1]);

  // 3등 점수 찾기
  let cutoffScore = 0;
  if (sortedScores.length >= 3) {
    cutoffScore = sortedScores[2][1]; // 3번째 항목 점수
  }

  // ✅ 3등 점수 이상인 모든 카테고리 출력
  const topCategories = sortedScores.filter(([_, score]) => score >= cutoffScore);

  

  // 📌 return은 함수 안에서 딱 한 번만!
  return (
    <div className="quizresult">
      <Header isLoggedIn={isLoggedIn} />

      <main className="quizresult-main">
        <div className="quizresult-card">
          <h2 className="quizresult-title">당신의 책BTI는 ...</h2>
          <h3 className="quizresult-subtitle">{result.type}</h3>

          {/* 상위 카테고리 태그 (퍼센트 포함) */}
          <div className="quizresult-tags">
            {topCategories
            .filter(([_,percent])=>Number(percent)>0)
            .map(([category]) => (
              <span key={category}>
                #{categoryLabels[category]} ({percentages[category]}%)
              </span>
            ))}
          </div>

          <img
            src={result.image}
            alt={result.type}
            className="quizresult-image"
          />

          <p className="quizresult-desc">{result.description}</p>

          <hr className="quizresult-divider" />
          <h4 className="quizresult-recommend-title">
            당신에게 어울리는 책을 <span>AI</span>가 추천해보았어요 !!
          </h4>
          <div className="quizresult-books">
            <BookList
              books={dummyBooks}
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
