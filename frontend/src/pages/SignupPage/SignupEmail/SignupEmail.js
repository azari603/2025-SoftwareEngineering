import { useEffect, useState } from "react";
import { useLocation, useNavigate, useSearchParams } from "react-router-dom";
import "./SignupEmail.css";
import Button from "../../../components/Button/Button";
import emailIcon from "../../../assets/mail_sent.png";

export default function SignupEmail() {
  const navigate = useNavigate();
  const location=useLocation();
  const [searchParams] = useSearchParams();
  const [isVerified, setIsVerified] = useState(false);

  const usernameFromState=location.state?.username;
  const usernameFromStorage=sessionStorage.getItem("username");
  const username=usernameFromState || usernameFromStorage || null;

  const emailFromState = location.state?.email;
  const emailFromStorage = sessionStorage.getItem("email"); 
  const email = emailFromState || emailFromStorage || "";



  useEffect(() => {
    const verifiedParam = searchParams.get("verified");
    if (verifiedParam === "true") {
      setIsVerified(true); // 인증된 경우 버튼 활성화
    }
  }, [searchParams]);

  return (
    <div className="email-container">
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
        onClick={() => navigate("/signup/success",{state:{username}})}
        disabled={!isVerified} // 인증되지 않으면 비활성화 -> 시뮬레이션 시 url 뒤에 ?verified=true 입력 시 버튼 활성화 됨
      >
        다음
      </Button>

      {/*<p className="email-resend">
        인증 메일을 받지 못하셨나요?{" "}
        <a href="/" className="resend-link">
          인증메일 재발송
        </a>
      </p>*/}
    </div>
  );
}
