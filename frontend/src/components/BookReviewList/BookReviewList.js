import React from "react";
import profile_img from "../../assets/profile_img.png";
import { useAuth } from "../../context/AuthContext";
import "./BookReviewList.css";   
import { useNavigate } from "react-router-dom";

const base=process.env.REACT_APP_BASE_URL;
function fullUrl(path) {
  if (!path) return null;
  if (path.startsWith("http")) return path;
  return `${base}/${path}`; // base 붙이기
}

const ReviewItem = ({review}) => {
  const navigate=useNavigate();
  const {user}=useAuth();
  const handleClick=()=>{
    navigate(`/review/${review.id}`);
  };
  const nickname=user.nickname??"작성자"
  const profileImg=fullUrl(review.profileImage)??profile_img

  return (
    <div className="reviewCard" onClick={handleClick} style={{cursor:"pointer"}}>
      <div className="left">
        <div className="userInfo">
          <div className="profileCircle">
            <img src={profileImg} 
            onError={(e) => (e.target.src = profile_img)}
            alt="user"/>
          </div>
          <div className="userText">
            <span className="userName">{nickname}</span>
            <span className="date">{review.createdAt.split("T")[0]}</span>
          </div>
        </div>

        <h3 className="title">{review.title}</h3>
        <p className="preview">{review.excerpt}</p>
      </div>

      <div className="right">
        <img 
          src={review.book?.image} 
          alt={review.book?.name} 
          className="bookImg" 
        />
      </div>
    </div>
  );
};

export default ReviewItem;
