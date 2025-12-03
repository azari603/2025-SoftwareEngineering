import { useLocation, useNavigate, Link } from "react-router-dom";
import "./FindPasswordSuccessPage.css";
import logo from "../../assets/logo.png";
import mailIcon from "../../assets/mail_sent.png";
import Button from "../../components/Button/Button";
import axiosInstance from "../../api/axiosInstance";
import { useState } from "react";

export default function FindPasswordSuccessPage() {
  const { state } = useLocation();
  const email = state?.email;
  const navigate = useNavigate();

  const [resendMessage, setResendMessage] = useState("");

  const handleResend = async () => {
    setResendMessage("");

    try {
      const res = await axiosInstance.post("/auth/email/resend", { email });

      if (res.data.success) {
        setResendMessage("인증 이메일을 다시 전송했습니다.");
      }
    } catch (error) {
      const code = error.response?.data?.code;

      if (code === "EMAIL_ALREADY_VERIFIED")
        setResendMessage("이미 인증된 이메일입니다.");
      else
        setResendMessage("메일 전송 중 오류가 발생했습니다.");
    }
  };

  return (
    <div className="pw-success-container">
      <div className="pw-success-box">

        <Link to="/" className="logo">
          <img src={logo} alt="logo" className="logo-icon" />
          <span className="logo-text">CHEACK</span>
        </Link>

        <h2 className="pw-title">비밀번호 찾기</h2>

        <img src={mailIcon} alt="메일" className="pw-mail-icon" />

        <p className="pw-msg">
          <strong>{email}</strong> 으로<br />
          임시 비밀번호가 발송되었습니다.<br />
          메일함을 확인해주세요.
        </p>

        <p className="pw-resend">
          이메일을 받지 못하셨나요?{" "}
          <span className="resend-link" onClick={handleResend}>
            재전송하기
          </span>
        </p>

        {/* 재전송 알림 메시지 */}
        {resendMessage && (
          <p className="resend-result">{resendMessage}</p>
        )}

        <Button
          type="button"
          variant="login-btn"
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
