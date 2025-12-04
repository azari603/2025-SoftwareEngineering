import React, { useState, useEffect, useRef } from "react";
import { forwardRef, useImperativeHandle } from "react";
import { useNavigate } from "react-router-dom";
import { FaRegCommentDots } from "react-icons/fa";
import { FaRegHeart } from "react-icons/fa6";
import { IoNotificationsOutline } from "react-icons/io5";
import { IoPersonAdd } from "react-icons/io5";


import "./Alert.css";
import {
  getNotifications,
  readNotification,
  deleteNotification,
  readAllNotifications,
  getUserById
} from "../../api/authApi";

const Alert = ({ isOpen, setIsOpen }, ref) => {
  const [notifications, setNotifications] = useState([]);
  const wrapperRef = useRef(null);
  const navigate = useNavigate();

  // 알림 목록 불러오기
  const loadNotifications = async () => {
    try {
      const list = await getNotifications(0, 20);
      const unreadOnly = list.filter((n) => !n.read);
      setNotifications(unreadOnly);
    } catch (err) {
      console.error("알림 조회 실패:", err);
    }
  };

//알림 메세지
const formatContent = (n) => {
  const actor = n.actorNickname || "사용자";
  const title = n.reviewTitle|| "";

  if (n.type.includes("LIKE"))
    return `${actor} 님이 "${title}" 글에 좋아요를 눌렀습니다.`;

  if (n.type.includes("COMMENT"))
    return `${actor} 님이 "${title}" 글에 댓글을 작성했습니다.`;

  if (n.type.includes("FOLLOW"))
    return `${actor} 님이 회원님을 팔로우했습니다.`;

  return n.content || "";
};

  useImperativeHandle(ref, () => ({
    reload: () => loadNotifications()
  }));


  useImperativeHandle(ref, () => ({
    reload: () => loadNotifications()
  }));

  // 읽음 처리
  const handleRead = async (id) => {
    try {
      await readNotification(id);
      setNotifications((prev) => prev.filter((n) => n.id !== id));
    } catch (err) {
      console.error("읽음 처리 실패:", err);
    }
  };

  // 삭제
  const handleDelete = async (id) => {
    try {
      await deleteNotification(id);
      setNotifications((prev) => prev.filter((n) => n.id !== id));
    } catch (err) {
      console.error("삭제 실패:", err);
    }
  };

  // 외부 클릭 시 닫기
  useEffect(() => {
    function handleClickOutside(event) {
      if (
        wrapperRef.current &&
        !wrapperRef.current.contains(event.target) &&
        !event.target.classList.contains("alarm")
      ) {
        setIsOpen(false);
      }
    }

    if (isOpen) {
      document.addEventListener("mousedown", handleClickOutside);
      loadNotifications();
    }
    return () => document.removeEventListener("mousedown", handleClickOutside);
  }, [isOpen]);

  if (!isOpen) return null;

  // 아이콘
  const getIcon = (type) => {
    if (!type) return <IoNotificationsOutline size={20} />;

    if (type.includes("LIKE"))
      return <FaRegHeart size={20} color="#cd5e5e" />;

    if (type.includes("COMMENT"))
      return <FaRegCommentDots size={20} color="#767676" />;

    if(type.includes("FOLLOW"))
      return <IoPersonAdd size={20} color="#000000ff" />;

    return <IoNotificationsOutline size={20} />;
  };

  // 타입 → 제목
  const formatTitle = (type) => {
    if (!type) return "알림";
    if (type.includes("LIKE")) return "좋아요";
    if (type.includes("COMMENT")) return "댓글";
    if (type.includes("FOLLOW")) return "팔로우";
    return "알림";
  };

  return (
    <div className="alert-wrapper" ref={wrapperRef}>
      <div className="alert-popup">
        <div className="popup-header">
          <h3>알림</h3>
          <span
            className="read-all"
            onClick={async () => {
              try {
                await readAllNotifications();
                setNotifications([]);         
              } catch (err) {
                console.error("전체 읽음 처리 실패:", err);
              }
            }}
          >
            전체 읽음
          </span>


        </div>

        <div className="alert-list">
          {notifications.map((n) => (
            <div
              key={n.id}
              className={`alert-item ${n.read ? "read" : ""}`}
              onClick={() => {
                handleRead(n.id);
                  console.log(" 알림 타입:", n.type, " | actor:", n.actor);

                if(n.type=="FOLLOW"){  
                   const username=n.actorUsername;
                   if(username){
                    handleRead(n.id);
                    navigate(`/profile/${username}`);
                   }
                    return;
                }
                if (n.targetUrl) {
                  const fixed = n.targetUrl.replace("/reviews", "/review");
                  navigate(fixed);
                }
              }}
            >
              <div className="alert-top">
                <span className="icon">{getIcon(n.type)}</span>

                <div className="alert-title-area">
                  <span className="alert-title">{formatTitle(n.type)}</span>
                  <span className="alert-dot">·</span>
                  <span className="alert-time">
                    {new Date(n.createdAt).toLocaleString()}
                  </span>
                </div>
              </div>


              <div className="alert-message">{formatContent(n)}</div>

              <button
                className="delete-btn"
                onClick={(e) => {
                  e.stopPropagation();
                  handleDelete(n.id);
                }}
              >
                ×
              </button>
            </div>
          ))}

          {notifications.length === 0 && (
            <p className="empty">알림이 없습니다.</p>
          )}
        </div>
      </div>
    </div>
  );
};

export default forwardRef(Alert);
