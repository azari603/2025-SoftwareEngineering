import "./Header.css";
import logo from "../../../assets/logo.png"
import logo_white from "../../../assets/logo_white.png"
import userIcon from "../../../assets/user.png"
import alarmIcon from "../../../assets/alarm.png"
import { Link, useLocation } from "react-router-dom" 
import SearchBar from "../../SearchBar/SearchBar";
import Button from "../../Button/Button";
import Alert from "../../Alert/Alert";
import ProfileMenu from "../../ProfileMenu/ProfileMenu";
import { FaUser } from "react-icons/fa";
import { IoNotificationsOutline} from "react-icons/io5";
import { useAuth } from "../../../context/AuthContext";
import { useState, useRef, useEffect } from "react";
import { getUnreadCount } from "../../../api/authApi";  


const Header = ({ isTransparent }) => {
  const { isLoggedIn } = useAuth();
  const location = useLocation();
  const currentPath = location.pathname;

  const isActive = (paths) => {
    if (typeof paths === "string") {
      return currentPath.startsWith(paths);
    }
    return paths.some((p) => currentPath.startsWith(p));
  };


  const [isAlertOpen, setIsAlertOpen] = useState(false);
  const [isProfileOpen, setIsProfileOpen] = useState(false);
  const [unreadCount, setUnreadCount] = useState(0);   

  const alertRef = useRef(null);
  const handleOpenAlert = async () => {
    const next = !isAlertOpen;
    setIsAlertOpen(next);

    if (next) {
      setUnreadCount(0);            
      alertRef.current?.reload?.();  
    }
  };

  // 읽지 않은 알림 개수 불러오기
  const loadUnreadCount = async () => {
    try {
      const count = await getUnreadCount();
      console.log("읽지 않은 알림 개수:", count);  
      setUnreadCount(count);
    } catch (err) {
      console.error("읽지 않은 알림 수 조회 실패:", err);
    }
  };

  // 로그인 되어 있으면 헤더 처음 렌더링할 때 한번 가져오기
  useEffect(() => {
    if (isLoggedIn) {
      loadUnreadCount();
    }
  }, [isLoggedIn]);


  return (
    <header className={`header ${isTransparent ? "header--transparent" : ""}`}>
      <div className="header-left">
        {/* 로고 */}
        <Link to="/" className="logo">
          <img
            src={isTransparent ? logo_white : logo}
            alt="logo"
            className="logo-icon"
          />
          <span
            className={`home-logo-text ${
              isTransparent ? "home-logo-text--transparent" : ""
            }`}
          >
            CHAECK
          </span>
        </Link>

        {/* 메뉴 */}
        <nav className={`nav ${isTransparent?"nav--transparent":""}`}>
          <Link to={isLoggedIn ? "/profile/library" : "/login"}
          className={isActive(["/profile/library","profile/stats","profile/reviews"])?"active":""}>나의 서재</Link>
          <Link to="/feed"
          className={isActive(["/feed"])?"active":""}>둘러보기</Link>
          <Link to={isLoggedIn ? "/quiz/start" : "/login"}
          className={isActive(["/quiz"])?"active":""}>책BTI</Link>
        </nav>

        {!isTransparent && (
          <SearchBar variant="outlined" className="searchBar" />
        )}
      </div>

      <div className="header-right">
        {isLoggedIn ? (
          <div className="user-info">
            <Button variant="filled" to="/write/book" size="small">
              글쓰기
            </Button>

          <div style={{ position: "relative" }}>
            <div className="notification-wrapper" onClick={handleOpenAlert}>
              <IoNotificationsOutline className="alarm bell-icon"
              color={isTransparent ? "#ffffff":"#222"} />
              {unreadCount > 0 && <span className="red-dot" />}
            </div>

            {isAlertOpen && (
              <Alert
                ref={alertRef}
                isOpen={isAlertOpen}
                setIsOpen={setIsAlertOpen}
              />
            )}
          </div>
            <FaUser
              className="user icon"
              onClick={() => setIsProfileOpen(!isProfileOpen)}
            />
            {isProfileOpen && (
              <ProfileMenu
                isOpen={isProfileOpen}
                setIsOpen={setIsProfileOpen}
              />
            )}
          </div>
        ) : (
          <div className="auth">
            <Link to="/signup" className="signup">
              회원가입
            </Link>
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
