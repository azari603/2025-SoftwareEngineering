// src/pages/Auth/FindIdPage.js
import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import "./FindIdPage.css";
import InputField from "../../components/InputField/InputField";
import Button from "../../components/Button/Button";
import logo from "../../assets/logo.png";
import { findId } from "../../api/authApi"; 

export default function FindIdPage() {
  const navigate = useNavigate();

  const [email, setEmail] = useState("");
  const [emailError, setEmailError] = useState("");
  const [generalError, setGeneralError] = useState("");

  const validateEmail = (value) => {
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return emailRegex.test(value);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    setEmailError("");
    setGeneralError("");

    if (!validateEmail(email)) {
      setEmailError("이메일 형식이 올바르지 않습니다.");
      return;
    }

    const res = await findId(email);

    if (!res.ok) {
      // EMAIL_NOT_FOUND → 정책상 동일 메시지 사용
      setGeneralError("가입된 계정이 존재하지 않습니다.");
      return;
    }

    const username = res.data.username;

    // 성공 페이지로 이동
    navigate("/find-id/success", {
      state: { email, username },
    });
  };

  return (
    <div className="findid-container">
      <div className="findid-box">
        <Link to="/" className="logo">
          <img src={logo} alt="logo" className="logo-icon" />
          <span className="logo-text">CHEACK</span>
        </Link>

        <h2 className="findid-title">아이디 찾기</h2>
        <p className="findid-subtitle">
          회원가입 시 사용한 이메일을 입력해주세요.
        </p>

        <form onSubmit={handleSubmit}>
          <InputField
            type="text"
            placeholder="이메일을 입력하세요"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            error={emailError || generalError}
            required
          />

          <Button
            type="submit"
            variant="login-btn"
            size="large"
            disabled={!email}
          >
            아이디 찾기
          </Button>
        </form>
      </div>
    </div>
  );
}
