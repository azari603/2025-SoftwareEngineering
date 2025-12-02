import React, { useState, useEffect } from "react";
import "./QuizPage.css";
import bearImage from "../../../assets/bear2.png";
import { useNavigate } from "react-router-dom";
import bookbtiApi from "../../../api/bookbtiAPI";

export default function QuizPage() {
  const [questions, setQuestions] = useState([]);
  const [sessionId, setSessionId] = useState(null);
  const [currentIndex, setCurrentIndex] = useState(0);
  const navigate = useNavigate();

  useEffect(() => {
    console.log("🔥 현재 sessionId:", sessionId);
  }, [sessionId]);
  // 처음 로딩 시 질문 + 세션 생성
  useEffect(() => {
    const init = async () => {
      try {

        const sRes = await bookbtiApi.createSession();
        setSessionId(sRes.data.sessionId);
        const qRes = await bookbtiApi.getQuestions();
        console.log("🔥 질문 응답:", qRes.data);
        setQuestions(qRes.data);


      } catch (e) {
        console.error("초기 로딩 실패:", e);
      }
    };
    init();
  }, []);

  // 질문이 로딩되기 전에는 currentQuestion을 계산하면 안됨
  if (questions.length === 0) {
    return (
      <div className="quizpage">
        <main className="quizpage-main">
          <div className="quiz-card">
            <h2>문항을 불러오는 중입니다...</h2>
          </div>
        </main>
      </div>
    );
  }

  // 여기서 currentQuestion 선언해야 함 (로딩 후)
  if (!questions.length) {
    return <div>로딩 중...</div>;
  }
  const currentQuestion = questions[currentIndex];
  const options = [
    { id: 1, text: currentQuestion.optionA },
    { id: 2, text: currentQuestion.optionB },
    { id: 3, text: currentQuestion.optionC },
  ];


  // 선택 시 서버로 답변 제출
  const handleAnswerClick = async (option) => {
    const question = questions[currentIndex];

    await bookbtiApi.sendAnswer(sessionId, option.id);

    if (currentIndex === questions.length - 1) {
      // finish 호출
      const finishRes = await bookbtiApi.finish(sessionId);
      console.log("finishRes.data:", finishRes.data);
      const resultId=finishRes.data.resultId;
      const result=finishRes.data.result;
      navigate("/quiz/result", {
        state: { result: result,
        resultId:resultId
        },
      });
    } else {
      setCurrentIndex(currentIndex + 1);
    }
  };

  // 되돌리기
  const handlePrevClick = async () => {
    if (currentIndex === 0) return;
    await bookbtiApi.undo(sessionId);
    setCurrentIndex(currentIndex - 1);
  };

  return (
    <div className="quizpage">
      <main className="quizpage-main">
        <div className="quiz-card">
          <img src={bearImage} alt="곰" className="quiz-image" />

          {/* 진행바 */}
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

          {/* 질문 */}
          <h2 className="quiz-question">
            <span className="quiz-question-number">Q{currentQuestion.number}. </span>
            {currentQuestion.text}
          </h2>

          {/* 선택지 */}
          <div className="quiz-options">
            {options.map((option) => (
              <button
                key={option.id}
                className="quiz-option"
                onClick={() => handleAnswerClick(option)}
              >
                {option.text}
              </button>
            ))}
          </div>

          {/* 이전 버튼 */}
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
