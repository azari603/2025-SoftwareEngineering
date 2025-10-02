import React from "react";
import { useNavigate } from "react-router-dom";
import "./QuizStart.css";
import bearImage from "../../../assets/bear1.png"; 
import Header from "../../../components/Header/Header";

export default function QuizStart({ isLoggedIn = true }) {
  const navigate = useNavigate();

  return (
    <div className="quizstart">
      <Header isLoggedIn={isLoggedIn} />

      <main className="quizstart-main">
        <div className="quizstart-card">
          <img src={bearImage} alt="곰" className="quizstart-image" />
          <h2 className="quizstart-title">나는 어떤 책을 좋아할까?</h2>
          <p className="quizstart-subtitle">
            결과를 바탕으로 AI가 책을 추천해드려요
          </p>
          <button
            className="quizstart-button"
            onClick={() => navigate("/quiz")}
          >
            테스트 하러가기 &gt;
          </button>
        </div>
      </main>
    </div>
  );
}