import React, { use, useEffect, useState } from "react";
import { FaPencilAlt } from "react-icons/fa";
import { IoIosArrowDown } from "react-icons/io";
import { IoIosArrowBack } from "react-icons/io";
import GoalModal from "../../components/Modal/GoalModal/GoalModal";
import "./StatsPage.css";
import { fetchGoals, fetchOverview, fetchStars, fetchTimeline, fetchTopAuthors, updateMonthlyGoal } from "../../api/statsApi";


export default function StatsPage() {
  const [overview, setOverview] = useState(null);
  const [stars, setStars] = useState({});
  const [timeline, setTimeline] = useState(null);
  const [authors, setAuthors] = useState(null);
  const [goals, setGoals] = useState({
    goal:0,
    achieved:0,
    rate:0,
  });

  const [goalModalOpen, setGoalModalOpen] = useState(false);
  const [openAuthor, setOpenAuthor] = useState(null);
  
  //별점 최댓값 (0처리 방지)
  const maxCount = Math.max(...Object.values(stars));

  //1~12월 placeholder
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

  //목표 달성률 계산
  const calcRate = (achieved, goal) => {
    if(goal===0){
      return achieved===0?0:100;
    }
    const percent = Math.round((achieved / goal) * 100);
    return percent;  
  };

  useEffect(()=>{
    (async()=>{
      try{
        const data=await fetchOverview({
          period: "year",
        });

        setOverview({
          completedCount: data.completedCount,
          reviewCount: data.reviewCount,
          averageRating: data.averageRating,
          readingCount: data.readingCount,
        });

        const starsData=await fetchStars();
        const mappedStars={};
        starsData.counts.forEach((count, index)=>{
          const score=index+1;
          mappedStars[score]=count;
        })
        setStars(mappedStars);

        const timelineData = await fetchTimeline({
          granularity: "month",
          from: "2025-01",
          to: "2025-12",
        });
        const mappedTimeline=timelineData.completedByMonth.map((count,idx)=>{
          const month=String(idx+1).padStart(2,"0");
          return{
            month: `${timelineData.year}-${month}`,
            reviews: count,
          }
        })
        setTimeline(mappedTimeline);
        
        const authorData = await fetchTopAuthors(10);
        const mappedAuthors=authorData.authors.map(a=>({
          name:a.author,
          count: a.reviewCount
        }));
        setAuthors(mappedAuthors);

        const goalData=await fetchGoals("month");

        setGoals({
          goal: goalData.goal,
          completed:goalData.completed,
        })
      }catch(err){
        console.error("통계 조회 실패:",err);
      }
    })();
  },[]);

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
              <p className="stats-value">{overview.completedCount} 권</p>
            </div>

            {/* 서평 */}
            <div className="stats-card">
              <p className="stats-label">작성한 서평 수</p>
              <p className="stats-value">{overview.reviewCount} 개</p>
            </div>

            {/* 이번달 독서량 */}
            <div className="stats-card">
              <div className="card-header-row">
                <p className="stats-label">이번달 독서량</p>
              </div>
              <p className="stats-value">{goals.completed} 권</p>
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
                
                  <svg className="progress-svg" width="240" height="240">
                    <circle
                    className="bg"
                    cx="120"
                    cy="120"
                    r="95"
                    strokeWidth="24"
                    />
                    <circle
                    className="progress"
                    cx="120"
                    cy="120"
                    r="95"
                    strokeWidth="28"
                    style={{
                        strokeDashoffset: 597 - (597 *Math.min(calcRate(goals.completed, goals.goal),100)) / 100+1,
                    }}
                    />
                    <text x="120" y="130" textAnchor="middle" className="percent">
                    {calcRate(goals.completed, goals.goal)}%
                    </text>
                </svg>
                
                
            </div>

            <div className="stats-rating-card">
              <h3>도서별 평점</h3>
              <div className="rating-x-axis"></div>
              <div className="rating-y-axis" style={{left:"70px"}}></div>
              {Object.entries(stars)
              .filter(([score]) => Number(score) >= 1)  
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
                  /*onClick={() =>
                    setOpenAuthor(openAuthor === idx ? null : idx)
                  }*/
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
        onSubmit={async(value)=>{
          const newGoal=Number(value);
          const result=await updateMonthlyGoal(newGoal);
          if(!result.ok){
            alert("목표 설정 실패ㅠㅠ"+result.code);
            return;
          }
          setGoals((prev)=>({
            ...prev,
            goal:newGoal,
            rate:calcRate(prev.achieved,newGoal)
          }));
          
          setGoalModalOpen(false);
        }}
        />
    </div>
  );
}
