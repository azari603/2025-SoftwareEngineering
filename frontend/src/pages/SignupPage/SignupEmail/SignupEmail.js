import { useEffect, useState} from "react";
import { useLocation, useNavigate, useSearchParams,Link  } from "react-router-dom";
import "./SignupEmail.css";
import Button from "../../../components/Button/Button";
import emailIcon from "../../../assets/mail_sent.png";
import { resendVerifyEmail, checkEmailVerified } from "../../../api/authApi";
import logo from "../../../assets/logo.png";     

export default function SignupEmail() {
  const navigate = useNavigate();
  const location=useLocation();
  const [isVerified, setIsVerified] = useState(false);
  const [loading, setLoading]=useState(false);

  const email= location.state?.email;

  const handleResend = async (e) => {
      e.preventDefault(); // a 태그 링크 이동 막기
      if (!email) return alert("이메일 정보를 찾을 수 없습니다.");

      setLoading(true);
      const res = await resendVerifyEmail(email);
      setLoading(false);

      if (res.ok) {
        alert("인증 이메일을 다시 전송했습니다.");
      } else if (res.code === "EMAIL_ALREADY_VERIFIED") {
        alert("이미 인증된 이메일입니다.");
        setIsVerified(true);
      } else {
        alert(res.message || "다시 시도해주세요.");
      }
    };

    const handleNext = async () => {
    if (!email) {
      alert("이메일 정보를 찾을 수 없습니다.");
      return;
    }

    setLoading(true);
    try {
      const res = await checkEmailVerified(email);
      setLoading(false);

      if (res.verified) {
        // 인증 완료 → 성공 페이지 이동
        navigate("/signup/success");
      } else {
        // 인증 안 된 경우
        alert("아직 이메일 인증이 완료되지 않았습니다.");
      }
    } catch (err) {
      setLoading(false);
      alert(err.message || "이메일 인증 확인 중 오류가 발생했습니다.");
    }
  };

  return (
    <div className="signup-email-container">
      <div className="signup-email-box">
        {/* 로고 영역 */}
        <div className="signup-logo">
          <Link to="/" className="logo">
            <img src={logo} alt="logo" className="logo-icon" />
            <span className="logo-text">CHEACK</span>
          </Link>
        </div>
      <img src={emailIcon} alt="메일 아이콘" className="email-icon" />

      <p className="email-text">
        <strong>{email}</strong> 으로<br />
        본인확인 이메일이 발송되었습니다.<br />
        메일함을 확인해주세요.
      </p>

      <Button
        type="button"
        variant="filled"
        size="small"
        onClick={handleNext}
      >
        다음
      </Button>

      {<p className="email-resend">
        인증 메일을 받지 못하셨나요?{" "}
        <a href="/" className="resend-link" onClick={handleResend}>
          인증메일 재발송
        </a>
      </p>}
      </div>
    </div>
  );
}
