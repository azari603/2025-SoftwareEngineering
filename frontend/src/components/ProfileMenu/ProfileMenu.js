import { useEffect, useRef } from "react";
import {useAuth} from "../../context/AuthContext"
import { useNavigate } from "react-router-dom";
import "./ProfileMenu.css";

const ProfileMenu = ({isOpen,setIsOpen}) => {
  const menuRef = useRef();
  const navigate=useNavigate();
  const {logout}=useAuth();

  const handleLogout=()=>{
    logout();
    setIsOpen(false);
    navigate('/');
  }

  // 바깥 클릭 시 닫히게 하기
  useEffect(() => {
    function handleClickOutside(e) {
      if (menuRef.current && !menuRef.current.contains(e.target) &&
    !e.target.classList.contains("user")) {
        setIsOpen(false);
      }
    }
    document.addEventListener("mousedown", handleClickOutside);
    return () => {
      document.removeEventListener("mousedown", handleClickOutside);
    };
  }, [setIsOpen]);

  if(!isOpen) return null;
  
  return (
    <div className="profile-wrapper" ref={menuRef}>

      {/* 팝업 메뉴 */}
      {isOpen && (
        <div className="profile-popup">
          <ul className="profile-list">
            <li className="profile-item">내 프로필</li>
            <li className="profile-item">계정 설정</li>
            <li className="profile-item logout"
            onClick={handleLogout}>로그아웃</li>
          </ul>
        </div>
      )}
    </div>
  );
};

export default ProfileMenu;
