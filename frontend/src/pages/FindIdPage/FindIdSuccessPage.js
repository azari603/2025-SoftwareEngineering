import { useLocation, useNavigate, Link } from "react-router-dom";
import "./FindIdSuccessPage.css";
import logo from "../../assets/logo.png";
import Button from "../../components/Button/Button";

export default function FindIdSuccessPage() {
  const location = useLocation();
  const navigate = useNavigate();

  const username = location.state?.username || "";

  return (
    <div className="findid-success-container">
      <div className="findid-success-box">

        {/* 로고 */}
        <Link to="/" className="logo">
          <img src={logo} alt="logo" className="logo-icon" />
          <span className="logo-text">CHEACK</span>
        </Link>


        {/* 아이디 출력 */}
        <p className="found-id-text">
          아이디 : <strong>{username}</strong>
        </p>

        {/* 버튼 */}
        <Button
          type="button"
          variant="filled"
          size="medium"
          onClick={() => navigate("/login")}
        >
          로그인하러 가기
        </Button>

        {/* 하단 안내 */}
        <div className="bottom-text">
          비밀번호가 기억 안 나시나요?{" "}
          <span className="pw-link" onClick={() => navigate("/find-password")}>
            비밀번호 찾기
          </span>
        </div>
      </div>
    </div>
  );
}
