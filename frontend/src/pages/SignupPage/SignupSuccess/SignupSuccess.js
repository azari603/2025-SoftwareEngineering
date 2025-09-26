import React from "react";
import { useLocation, useNavigate } from "react-router-dom";
import "./SignupSuccess.css";
import Button from "../../../components/Button/Button";
import successIcon from "../../../assets/check.png";

export default function SignupSuccess() {
  const location = useLocation();
  const navigate=useNavigate();
  
  const usernameFromState=location.state?.username;
  const usernameFromStorage=sessionStorage.getItem("username");
  const username = usernameFromState || usernameFromStorage || "회원";

  return (
    <div className="success-container">
      <h2 className="success-title">회원가입 완료!</h2>

      <img src={successIcon} alt="성공 아이콘" className="success-icon" />

      <p className="success-welcome">
        환영합니다, {username} 님 !
      </p>

      <p className="success-subtext">
        책을 읽는 순간은 흘러가지만.<br />
        기록은 언제든 곁에 남습니다.<br />
        오늘부터 함께 쌓아갈 여정을 나아가요.
      </p>

      <Button type="button" variant="filled" size="medium"
      onClick={()=> navigate("/login")}>
        로그인 하러가기
      </Button>
    </div>
  );
}
