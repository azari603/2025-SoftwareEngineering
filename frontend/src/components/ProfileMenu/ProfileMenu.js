import React, { useState, useEffect, useRef } from "react";
import "./ProfileMenu.css";

const ProfileMenu = () => {
  const [isOpen, setIsOpen] = useState(false);
  const menuRef = useRef();

  // 바깥 클릭 시 닫히게 하기
  useEffect(() => {
    function handleClickOutside(e) {
      if (menuRef.current && !menuRef.current.contains(e.target)) {
        setIsOpen(false);
      }
    }
    document.addEventListener("mousedown", handleClickOutside);
    return () => {
      document.removeEventListener("mousedown", handleClickOutside);
    };
  }, []);

  return (
    <div className="profile-wrapper" ref={menuRef}>
      {/* 👤 버튼 */}
      <button className="profile-btn" onClick={() => setIsOpen(!isOpen)}>
        👤
      </button>

      {/* 팝업 메뉴 */}
      {isOpen && (
        <div className="profile-popup">
          <ul className="profile-list">
            <li className="profile-item">내 프로필</li>
            <li className="profile-item">계정 설정</li>
            <li className="profile-item logout">로그아웃</li>
          </ul>
        </div>
      )}
    </div>
  );
};

export default ProfileMenu;
