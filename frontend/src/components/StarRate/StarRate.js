import React, { useState, useEffect } from "react";
// react-icons에서 별 아이콘 3종 불러오기
// - FaStar: 꽉 찬 별
// - FaStarHalfAlt: 반쪽 별
import { FaStar, FaStarHalfAlt } from "react-icons/fa";
import "./StarRate.css"

const StarRate = ({ value = 0, readOnly = false, onChange}) => {
  const [rating, setRating] = useState(value);

  // 외부에서 전달된 value 값이 바뀌면 내부 rating 동기화
  useEffect(() => {
    setRating(value);
  }, [value]);

  // 별 클릭 시 실행되는 함수
  const handleClick = (e, index) => {
    if (readOnly) return; // 읽기 전용이면 클릭 막기

    const rect = e.currentTarget.getBoundingClientRect(); // 클릭한 별의 위치/크기 정보
    const clickX = e.clientX - rect.left; // 클릭 좌표 (별의 왼쪽 기준)
    const half = clickX < rect.width / 2; // 클릭 위치가 별의 절반 이하인지 확인

    // 클릭 위치가 왼쪽이면 0.5점, 오른쪽이면 1점 단위로 계산
    const newRating = half ? index + 0.5 : index + 1;
    setRating(newRating);

    //부모에게 전달
    if (onChange) {
      onChange(newRating);
    }
  };

  return (
    <div className="rating-container">
      <div className="rating">
        {/* 5개의 별 생성 */}
        {[...Array(5)].map((_, index) => {
          const starValue = index + 1; // 각 별의 점수
          const isFull = rating >= starValue; // 꽉 찬 별 여부
          const isHalf = rating >= starValue - 0.5 && rating < starValue; // 반쪽 별 여부

          return (
            <span
              key={index}
              onClick={(e) => handleClick(e, index)} // 클릭 시 별점 업데이트
              style={{
                cursor: readOnly ? "default" : "pointer", // readOnly면 클릭 불가
                color: isFull || isHalf ? "#299159" : "#ccc", // 색상: 초록(선택됨) / 회색(선택 안됨)
                pointerEvents: readOnly ? "none" : "auto", // 클릭 차단 여부
              }}
            >
              {/* 조건에 따라 별 종류 렌더링 */}
              {isFull ? (
                <FaStar className="star-icon" /> // 꽉 찬 별
              ) : isHalf ? (
                <FaStarHalfAlt className="star-icon" /> // 반쪽 별
              ) : (
                <FaStar className="star-icon"/> // 빈 별
              )}
            </span>
          );
        })}

        {/* 별점 숫자 표시 */}
        <span className="score">
          <span className="score-number">{rating}</span>
          <span className="score-total"> / 5</span>
        </span>
      </div>
    </div>
  );
};

export default StarRate;
