import React, { useState, useEffect, useRef } from "react";
import "./Alert.css";

const Alert = () => {
  const [isOpen, setIsOpen] = useState(false);
  const [notifications, setNotifications] = useState([
    { id: 1, type:"like", time: "방금 전", text: "수진님이 ‘침묵속에서 들…’ 서평에 좋아요를 눌렀어요." },
    { id: 2, type: "comment", time: "어제 오후 6:30", text: "수진님 외 3명이 ‘침묵속에서 들…’ 서평에 댓글을 달았어요." },
    { id: 3, type:"like", time: "방금 전", text: "수진님이 ‘침묵속에서 들…’ 서평에 좋아요를 눌렀어요." },
    { id: 4, type: "comment", time: "어제 오후 6:30", text: "수진님 외 3명이 ‘침묵속에서 들…’ 서평에 댓글을 달았어요." }
  ]);

  const wrapperRef = useRef(null);
  const getEmoji = (type) => {
  switch (type) {
    case "like":
      return "❤️";
    case "comment":
      return "💬";
    default:
      return "🔔"; // 기본 아이콘
  }
};


  // 알림 삭제
  const removeNotification = (id) => {
    setNotifications(notifications.filter((n) => n.id !== id));
  };

  // 알림창 외부 클릭 시 닫기
  useEffect(() => {
    const handleClickOutside = (event) => {
      if (
        wrapperRef.current &&
        !wrapperRef.current.contains(event.target)
      ) {
        setIsOpen(false);
      }
    };

    if (isOpen) {
      document.addEventListener("mousedown", handleClickOutside);
    } else {
      document.removeEventListener("mousedown", handleClickOutside);
    }

    return () => {
      document.removeEventListener("mousedown", handleClickOutside);
    };
  }, [isOpen]);

  return (
    <div className="alert-wrapper" ref={wrapperRef}>
      {/* 오른쪽 상단 버튼 */}
      <button className="open-btn" onClick={() => setIsOpen(!isOpen)}>
        🔔
      </button>

      {/* 알림창 */}
      {isOpen && (
        <div className="alert-popup">
          <div className="popup-header">
            <h3>알림</h3>
          </div>

          <div className="alert-list">
            {notifications.map((n) => (
              <div key={n.id} className="alert-item">
                <span className="icon">{getEmoji(n.type)}</span>
                <div className="content">
                    <span className="time">{n.time}</span>
                    <p>{n.text}</p>
                </div>
                <button
                  className="delete-btn"
                  onClick={() => removeNotification(n.id)}
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
      )}
    </div>
  );
};

export default Alert;
