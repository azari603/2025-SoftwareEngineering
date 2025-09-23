import "./Header.css";
import logo from "../../assets/logo.png"
import { Link } from "react-router-dom" 
import SearchBar from "../SearchBar/SearchBar";
import Button from "../Button/Button"

const Header = () => {
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
        {/* 회원가입/로그인 */}
        <div className="auth">
          <Link to="/signup" className="signup">회원가입</Link>
          <Button variant="filled" to="/login" disabled={true}>로그인</Button>
        </div>
      </div>
      
    </header>
  );
};

export default Header;
