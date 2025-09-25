// src/components/ListLayout/CarouselLayout.jsx
import { useState, useRef, useEffect } from "react";
import arrowRight from "../../assets/arrowRight.png";
import arrowLeft from "../../assets/arrowLeft.png";
import "./CarouselList.css";

const CarouselLayout = ({ items, renderItem, visibleCount = 3 }) => {
  const [startIndex, setStartIndex] = useState(0);
  const [rightBtnPos, setRightBtnPos] = useState(0);
  const containerRef = useRef(null);
  const lastItemRef = useRef(null);

  const handlePrev = () => {
    if (startIndex > 0) setStartIndex(startIndex - 1);
  };

  const handleNext = () => {
    if (startIndex < items.length - visibleCount) {
      setStartIndex(startIndex + 1);
    }
  };

  const visibleItems = items.slice(startIndex, startIndex + visibleCount);

  useEffect(() => {
    if (lastItemRef.current && containerRef.current) {
      const containerRect = containerRef.current.getBoundingClientRect();
      const lastItemRect = lastItemRef.current.getBoundingClientRect();
      const pos = lastItemRect.right - containerRect.left;
      setRightBtnPos(pos);
    }
  }, [visibleItems]);

  const showLeftBtn = startIndex > 0;
  const showRightBtn = startIndex < items.length - visibleCount;

  return (
    <div className="carousel-layout" ref={containerRef}>
      {showLeftBtn && (
        <button className="nav-button left" onClick={handlePrev}>
          <img src={arrowLeft} alt="이전" />
        </button>
      )}

      <div className="carousel-container">
        {visibleItems.map((item, idx) =>
          renderItem(item, idx, idx === visibleItems.length - 1 ? lastItemRef : null)
        )}
      </div>

      {showRightBtn && (
        <button
          className="nav-button right"
          onClick={handleNext}
          style={{ left: `${rightBtnPos - 30}px` }}
        >
          <img src={arrowRight} alt="다음" />
        </button>
      )}
    </div>
  );
};

export default CarouselLayout;
