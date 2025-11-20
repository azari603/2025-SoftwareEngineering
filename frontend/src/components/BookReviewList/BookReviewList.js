import React from "react";
import "./BookReviewList.css";   
import { useNavigate } from "react-router-dom";

const ReviewItem = ({ review}) => {
  const navigate=useNavigate();
  const handleClick=()=>{
    navigate(`/review/${review.reviewId}`);
  };

  return (
    <div className="reviewCard" onClick={handleClick} style={{cursor:"pointer"}}>
      <div className="left">
        <div className="userInfo">
          <div className="profileCircle">
            <img src={review.user.profileImg} alt="user"/>
          </div>
          <div className="userText">
            <span className="userName">{review.user.nickname}</span>
            <span className="date">{review.createdAt}</span>
          </div>
        </div>

        <h3 className="title">{review.title}</h3>
        <p className="preview">{review.preview}</p>
      </div>

      <div className="right">
        <img 
          src={review.book?.image} 
          alt={review.book?.title} 
          className="bookImg" 
        />
      </div>
    </div>
  );
};

export default ReviewItem;
