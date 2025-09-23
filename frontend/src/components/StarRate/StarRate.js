import React, { useState } from "react";
import "./StarRate.css";

const StarRate = () => {
  const [rating, setRating] = useState(0); //초기값 0점

  const handleClick = (e, index) => {  //별 클릭할 경우 실행
    const rect = e.currentTarget.getBoundingClientRect();  //별의 위치와 크기 가져오기
    const clickX = e.clientX - rect.left;  //별 좌표 왼쪽에서 얼마나 떨어져 있는지

    if (clickX < rect.width / 2) {  //별 왼쪽 클릭 -> 0.5점
      setRating(index + 0.5);
    } else {
      setRating(index + 1);  //별 오른쪽 클릭 -> 1점
    }
  };

  return (
    <div className="rating-container">
      <div className="rating">
        {[...Array(5)].map((_, index) => {   //array 만들어서 별 다섯개 생성
          const value = index + 1;  //별 번호 붙히기
          let fill = "#ccc"; // 선택 안 된 별 -> 회색
          if (rating >= value) {  // rating 값이 별 번호보다 크면 -> 선택됨
            fill = "#299159"; // 선택 된 별 -> 초록색
          } else if (rating >= value - 0.5) {  // rating 값이 별 번호보다 0.5 작은 경우 -> 반쪽 별
            // 반쪽 별
            return (
              <svg
                key={index}
                onClick={(e) => handleClick(e, index)} 
                xmlns="http://www.w3.org/2000/svg"
                viewBox="0 0 24 24"
                className="star-icon"
              >
                <defs>
                  <linearGradient id={`half-${index}`}>
                    <stop offset="50%" stopColor="#299159" />
                    <stop offset="50%" stopColor="#ccc" />
                  </linearGradient>
                </defs>
                <path
                  fill={`url(#half-${index})`}
                  d="M12 .587l3.668 7.431 8.2 1.192-5.934 5.782 
                     1.402 8.178L12 18.896l-7.336 3.854 
                     1.402-8.178L.132 9.21l8.2-1.192z"
                />
              </svg>
            );
          }

          // 다 찬 별
          return (
            <svg
              key={index}
              onClick={(e) => handleClick(e, index)}
              xmlns="http://www.w3.org/2000/svg"
              viewBox="0 0 24 24"
              className="star-icon"
            >
              <path
                fill={fill}
                d="M12 .587l3.668 7.431 8.2 1.192-5.934 5.782 
                   1.402 8.178L12 18.896l-7.336 3.854 
                   1.402-8.178L.132 9.21l8.2-1.192z"
              />
            </svg>
          );
        })}
        <span className="score">
          <span className="score-number">{rating}</span>
          <span className="score-total"> / 5</span>
        </span>
      </div>
    </div>
  );
};

export default StarRate;
