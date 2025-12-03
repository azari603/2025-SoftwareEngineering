import { useState } from "react";
import "./Header.css";
import logo from "../../../assets/logo.png"
import logo_white from "../../../assets/logo_white.png"
import userIcon from "../../../assets/user.png"
import alarmIcon from "../../../assets/alarm.png"
import { Link } from "react-router-dom" 
import SearchBar from "../../SearchBar/SearchBar";
import Button from "../../Button/Button";
import Alert from "../../Alert/Alert";
import ProfileMenu from "../../ProfileMenu/ProfileMenu";
import { FaUser } from "react-icons/fa";
import { IoNotificationsOutline } from "react-icons/io5";
import {useAuth} from '../../../context/AuthContext'

const Header = ({isTransparent}) => {
  /*useAuth 훅을 사용하여 isLoggined랑 logout 함수 가져오기*/
  const {isLoggedIn}=useAuth();
  /* 알림창, 프로필 메뉴 열렸는지 상태 확인 배열 */
  const [isAlertOpen, setIsAlertOpen]=useState(false);
  const [isProfileOpen, setIsProfileOpen] =useState(false);
  return (
    <header className={`header ${isTransparent?"header--transparent":""}`}>
      <div className="header-left">
        {/* 로고 */}
        <Link to="/" className="logo">
          <img src={isTransparent?logo_white:logo} alt="logo" className="logo-icon" />
          <span className={`home-logo-text ${isTransparent?"home-logo-text--transparent":""}`}>CHAECK</span>
        </Link>

        {/* 메뉴 */}
        <nav className={`nav ${isTransparent?"nav--transparent":""}`}>
          <Link to={isLoggedIn ? "/profile/library" : "/login"}>나의 서재</Link>
          <Link to="/feed">둘러보기</Link>
          <Link to={isLoggedIn ? "/quiz/start" : "/login"}>책BTI</Link>
        </nav>
        
        {!isTransparent&&(
          <SearchBar variant="outlined" className="searchBar"/>
        )}
        
      </div>

      
      <div className="header-right">
        {isLoggedIn ? (
          // 로그인 후 UI
          <div className="user-info">
            <Button
              variant="filled" to="/write/book" size="small">글쓰기</Button> 
            <div style={{position:"relative"}}> 
              <IoNotificationsOutline className="alarm icon"  onClick={()=> setIsAlertOpen(!isAlertOpen)}/>
              {isAlertOpen && <Alert isOpen ={isAlertOpen} setIsOpen={setIsAlertOpen} />}
            </div>
            <FaUser className="user icon" onClick={() => setIsProfileOpen(!isProfileOpen)}/>
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
