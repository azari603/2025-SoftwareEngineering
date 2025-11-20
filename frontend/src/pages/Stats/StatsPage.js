import React, { useEffect, useState } from "react";
import { FaPencilAlt } from "react-icons/fa";
import { IoIosArrowDown } from "react-icons/io";
import { IoIosArrowBack } from "react-icons/io";
import GoalModal from "../../components/Modal/GoalModal/GoalModal";
import "./StatsPage.css";
import {
  dummyOverview,
  dummyStars,
  dummyTimeline,
  dummyAuthors,
  dummyGoals,
} from "../../mocks/dummyStats";


export default function StatsPage() {
  const [overview, setOverview] = useState(null);
  const [stars, setStars] = useState({});
  const [timeline, setTimeline] = useState(null);
  const [authors, setAuthors] = useState(null);
  const [goals, setGoals] = useState(null);

  const [goalModalOpen, setGoalModalOpen] = useState(false);
  const [openAuthor, setOpenAuthor] = useState(null);
  const maxCount = Math.max(...Object.values(stars));
  const fullYear = timeline
    ? Array.from({ length: 12 }, (_, i) => {
        const month = String(i + 1).padStart(2, "0");
        const found = timeline.find((t) => t.month.endsWith(month));
        return found || { month: `2025-${month}`, reviews: 0 };
      })
    : [];

  const topAuthors = authors?
  authors.sort((a, b) => b.count - a.count)
  .slice(0, 4)
  :[];


  useEffect(() => {
    setOverview(dummyOverview);
    setStars(dummyStars);
    setTimeline(dummyTimeline);
    setAuthors(dummyAuthors);
    setGoals(dummyGoals);
  }, []);

  if (!overview) return <div>Loading...</div>;

  return (
    <div className="stats-container">
      <main className="stats-main">
        <div className="stats-header">
          <h2>
            나의 기록 &gt; <span className="stats-breadcrumb">독서 통계</span>
          </h2>
        </div>

        <div className="stats-wrapper">

          {/* ---- 네 개 카드 ---- */}
          <div className="stats-top-cards">

            {/* 완독 */}
            <div className="stats-card">
              <p className="stats-label">완독 도서</p>
              <p className="stats-value">{overview.totalRead} 권</p>
            </div>

            {/* 서평 */}
            <div className="stats-card">
              <p className="stats-label">작성한 서평 수</p>
              <p className="stats-value">{overview.timeline.length} 개</p>
            </div>

            {/* 이번달 독서량 */}
            <div className="stats-card">
              <div className="card-header-row">
                <p className="stats-label">이번달 독서량</p>
              </div>
              <p className="stats-value">{goals.achieved} 권</p>
            </div>

            {/* 이번달 목표 */}
            <div className="stats-card editable-card">
              <p className="stats-label">이번달 목표 독서량
                <span className="edit-icon" onClick={() => setGoalModalOpen(true)}>
                  <FaPencilAlt />
                </span>
                </p>
              <p className="stats-value">{goals.goal} 권</p>
            </div>
          </div>

          {/* ---- 원형 그래프 + 도서별 평점 ---- */}
          <div className="stats-mid-row">
            <div className="stats-circle-card">
                <h3>이번달 목표 달성률</h3>

                <svg className="progress-svg" width="160" height="160">
                    <circle
                    className="bg"
                    cx="80"
                    cy="80"
                    r="60"
                    strokeWidth="15"
                    />
                    <circle
                    className="progress"
                    cx="80"
                    cy="80"
                    r="60"
                    strokeWidth="18"
                    style={{
                        strokeDashoffset: 377 - (377 * goals.rate) / 100,
                    }}
                    />
                    <text x="80" y="90" textAnchor="middle" className="percent">
                    {goals.rate}%
                    </text>
                </svg>
            </div>

            <div className="stats-rating-card">
              <h3>도서별 평점</h3>
              <div className="rating-x-axis"></div>
              <div className="rating-y-axis" style={{left:"70px"}}></div>
              {Object.entries(stars)
              .filter(([score]) => Number(score) >= 2)  
              .sort(([a], [b]) => Number(b) - Number(a)) 
              .map(([score, count]) => (
                <div className="rating-row" key={score}>
                  <span className="rating-star">{score}점</span>
                  <div className="bar-container">
                    <div
                      className={`rating-bar bar-${score}`}
                      style={{ width: `${(count / maxCount)*80}%` }}
                    >
                      <span className="rating-count">{count}</span>
                    </div>
                  </div>
                  
                </div>
              ))}
              </div>
          </div>

          {/* ---- 올해 기록 ---- */}
          <div className="stats-year-box">
            <h3>올해의 기록 현황</h3>
            {timeline && (
              <div className="year-chart">
                <div className="year-y-axis"></div>
                <div className="year-x-axis"></div>
                {fullYear.map((item, idx) => {
                  const maxReviews = Math.max(...timeline.map(t => t.reviews));
                  const heightPercent = (item.reviews / maxReviews) * 100;

                  return (
                    <div className="year-col" key={idx}>
                      <div
                        className="year-bar"
                        style={{ height: `${heightPercent}%` }}
                      >
                        {item.reviews > 0 && (
                          <span className="year-count">{item.reviews}</span>
                        )}
                      </div>

                      <span className="year-month">{item.month.slice(5)}월</span>
                    </div>
                  );
                })}
              </div>
            )}
          </div>


          {/* ---- 자주 읽은 저자 ---- */}
          <div className="stats-author-box">
            <h3>자주 읽은 저자</h3>

            {topAuthors.map((author, idx) => (
              <div key={idx} className="author-item">
                <div
                  className="author-header"
                  onClick={() =>
                    setOpenAuthor(openAuthor === idx ? null : idx)
                  }
                >
                  <span>{idx + 1}. {author.name}</span>
                  <span className="author-book-count">
                    {author.count}권 
                    {openAuthor === idx ? <IoIosArrowBack /> : <IoIosArrowDown />}
                  </span>

                </div>
                {openAuthor === idx && (
                  <div className="author-books">
                    {author.books.map((b, i) => (
                      <div className="author-book-item" key={i}>
                        <img src={b.image}
                        alt={b.title} className="author-book-cover"/>
                        <span className="author-book-title">{b.title}</span>
                      </div>
                    ))}
                  </div>
                )}
              </div>
            ))}
          </div>

        </div>
      </main>

      {/* ---- 목표 수정 모달 ---- */}
      <GoalModal
        isOpen={goalModalOpen}
        onClose={()=>setGoalModalOpen(false)}
        onSubmit={(value)=>{
          const newGoal=Number(value);
          setGoals(prev=>({
            ...prev,
            goal:newGoal,
            rate:Math.round((prev.achieved/newGoal)*100)
          }));
          console.log("입력된 목표 : ",value);
          setGoalModalOpen(false);
        }}
        />
    </div>
  );
}
