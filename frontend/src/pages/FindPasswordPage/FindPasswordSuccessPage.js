import { useLocation, useNavigate, Link } from "react-router-dom";
import "./FindPasswordSuccessPage.css";
import logo from "../../assets/logo.png";
import mailIcon from "../../assets/mail_sent.png";
import Button from "../../components/Button/Button";

export default function FindPasswordSuccessPage() {
  const { state } = useLocation();
  const email = state?.email;
  const navigate = useNavigate();

  return (
    <div className="pw-success-container">
      <div className="pw-success-box">

        {/* 로고 */}
        <Link to="/" className="logo">
          <img src={logo} alt="logo" className="logo-icon" />
          <span className="logo-text">CHEACK</span>
        </Link>

        <h2 className="pw-title">비밀번호 찾기</h2>

        <img src={mailIcon} alt="메일" className="pw-mail-icon" />

        <p className="pw-msg">
          <strong>{email}</strong> 으로<br/> 임시 비밀번호가 전송되었습니다.<br />
          메일함을 확인해주세요.
        </p>

        <p className="pw-resend">
          이메일을 받지 못하셨나요?{" "}
          <span className="resend-link">재전송하기</span>
        </p>

        <Button
          type="button"
          variant="filled"
          size="medium"
          onClick={() => navigate("/login")}
        >
          로그인하러 가기
        </Button>

        <div className="pw-home" onClick={() => navigate("/")}>
          홈으로 돌아가기
        </div>
      </div>
    </div>
  );
}
