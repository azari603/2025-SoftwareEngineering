import React, { useState } from "react";
import "./QuizPage.css";
import bearImage from "../../../assets/bear2.png"; // 곰 이미지
import Header from "../../../components/Header/Header";
import {useNavigate} from "react-router-dom";
import dummyOptions from "../../../mocks/dummyOptions";


export default function QuizPage({ isLoggedIn =   true }) {
  

  const [currentIndex, setCurrentIndex] = useState(0);
  const questions=dummyOptions;
  const currentQuestion = questions[currentIndex];
  const [scores, setScores] = useState({
    future: 0,
    romance: 0,
    history: 0,
    fantasy: 0,
    thriller: 0,
    science: 0,
  });


  const navigate=useNavigate();
    const handleAnswerClick = (option) => {
      let newScores;

      // 멀티 카테고리 처리
      if (option.multi) {
        newScores = { ...scores };
        for (const [cat, val] of Object.entries(option.multi)) {
          newScores[cat] += val;
        }
      } else {
        // 단일 카테고리 처리
        newScores = {
          ...scores,
          [option.category]: scores[option.category] + option.score,
        };
      }

      setScores(newScores); // ✅ 점수는 여기서 한 번만 업데이트
      console.log("선택된 옵션:", option);

      if (currentIndex === questions.length - 1) {
        // 마지막 문제 → 결과 화면으로 이동
        const totalScore = Object.values(newScores).reduce((sum, v) => sum + v, 0);
        const percentages = Object.fromEntries(
          Object.entries(newScores).map(([category, score]) => [
            category,
            ((score / totalScore) * 100).toFixed(1),
          ])
        );
        const topCategory = Object.entries(newScores).sort((a, b) => b[1] - a[1])[0][0];

        navigate("/quiz/result", {
          state: { scores: newScores, percentages, resultKey: topCategory },
        });
      } else {
        // 다음 문제로 이동
        setCurrentIndex(currentIndex + 1);
      }
    };

  const handlePrevClick = () => {
    if (currentIndex > 0) {
      setCurrentIndex(currentIndex - 1);
    }
  };

  return (
    <div className="quizpage">
      <Header isLoggedIn={isLoggedIn} />

      <main className="quizpage-main">
        <div className="quiz-card">
          <img src={bearImage} alt="곰" className="quiz-image" />

          {/* ✅ 진행 바 + 숫자 같이 */}
          <div className="quiz-progressbar-wrapper">
            <div className="quiz-progressbar">
              <div
                className="quiz-progressbar-fill"
                style={{
                  width: `${((currentIndex + 1) / questions.length) * 100}%`,
                }}
              ></div>
            </div>
            <div className="quiz-progress-text">
              {currentIndex + 1}/{questions.length}
            </div>
          </div>

          <h2 className="quiz-question">
            <span className="quiz-question-number">
              Q{currentQuestion.id}. 
            </span>
            {currentQuestion.text}
            
          </h2>
          <div className="quiz-options">
            {currentQuestion.options.map((option, i) => (
              <button
                key={i}
                className="quiz-option"
                onClick={() => handleAnswerClick(option)}
              >
                {option.text}
              </button>
            ))}
          </div>

          {/* 이전 문제 버튼 */}
          <button
            className="quiz-prev-btn"
            onClick={handlePrevClick}
            disabled={currentIndex === 0}
          >
            ←
          </button>
        </div>
      </main>
    </div>
  );
}