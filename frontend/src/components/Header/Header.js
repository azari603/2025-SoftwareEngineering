import React, { useState } from "react";
import "./Header.css";
import logo from "../../assets/logo.png"
import userIcon from "../../assets/user.png"
import alarmIcon from "../../assets/alarm.png"
import { Link } from "react-router-dom" 
import SearchBar from "../SearchBar/SearchBar";
import Button from "../Button/Button";
import Alert from "../Alert/Alert";
import ProfileMenu from "../ProfileMenu/ProfileMenu";




const Header = () => {
  const isLoggedIn=true;
  /* 알림창, 프로필 메뉴 열렸는지 상태 확인 배열 */
  const [isAlertOpen, setIsAlertOpen]=useState(false);
  const [isProfileOpen, setIsProfileOpen] =useState(false);
  return (
    <header className="header">
      <div className="header-left">
        {/* 로고 */}
        <Link to="/" className="logo">
          <img src={logo} alt="logo" className="logo-icon" />
          <span className="logo-text">CHAECK</span>
        </Link>

        {/* 메뉴 */}
        <nav className="nav">
          <Link to="/myLibrary">나의 서재</Link>
          <Link to="/explore">둘러보기</Link>
          <Link to="/bookbt">책BTI</Link>
        </nav>

        {/*검색창*/}
        <SearchBar variant="outlined" className="searchBar"/>
      </div>

      
      <div className="header-right">
        {isLoggedIn ? (
          // 로그인 후 UI
          <div className="user-info">
            <Button
              variant="filled" to="/write" size="small">글쓰기</Button> 
            <div style={{position:"relative"}}> 
              <img src={alarmIcon} alt="alarmIcon" className="alarm icon" onClick={()=> setIsAlertOpen(!isAlertOpen)}/>
              {isAlertOpen && <Alert isOpen ={isAlertOpen} setIsOpen={setIsAlertOpen} />}
            </div>
            <img src={userIcon} alt="userIcon" className="user icon" onClick={() => setIsProfileOpen(!isProfileOpen)}/>
            {isProfileOpen && <ProfileMenu isOpen={isProfileOpen} setIsOpen={setIsProfileOpen} />}
          </div>
        ) : (
          // 로그인 전 UI
          <div className="auth">
            <Link to="/signup" className="signup">회원가입</Link>
            <Button variant="filled" to="/login" size="small">
              로그인
            </Button>
          </div>
        )}
      </div>
      
    </header>
  );
};

export default Header;
