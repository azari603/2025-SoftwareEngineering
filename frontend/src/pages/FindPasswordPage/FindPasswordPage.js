import { useState } from "react";
import { useNavigate, Link } from "react-router-dom";
import "./FindPasswordPage.css";
import InputField from "../../components/InputField/InputField";
import Button from "../../components/Button/Button";
import logo from "../../assets/logo.png";
import axiosInstance from "../../api/axiosInstance";

export default function FindPasswordPage() {
  const navigate = useNavigate();

  const [username, setUsername] = useState("");
  const [email, setEmail] = useState("");

  const [errorMsg, setErrorMsg] = useState("");

  const handleSubmit = async (e) => {
    e.preventDefault();
    setErrorMsg("");

    try {
      const res = await axiosInstance.post("/auth/password/forgot", {
        email: email,
      });

      if (res.data.success) {
        navigate("/find-password/success", {
          state: { email },
        });
      }
    } catch (err) {
      const code = err.response?.data?.code;

      if (code === "USER_NOT_FOUND") {
        setErrorMsg("일치하는 계정을 찾을 수 없습니다.");
      } else {
        setErrorMsg("서버 오류가 발생했습니다.");
      }
    }
  };

  return (
    <div className="findpw-container">
      <div className="findpw-box">

        <div className="findpw-logo-wrapper">
        <Link to="/" className="logo">
            <img src={logo} alt="logo" className="logo-icon" />
            <span className="logo-text">CHEACK</span>
        </Link>
        </div>

        <h2 className="findpw-title">비밀번호 찾기</h2>

        <p className="findpw-sub">
          임시 비밀번호 발급을 위해 이메일을 입력해주세요.
        </p>

        <form onSubmit={handleSubmit}>
          <div className="field-label">이메일</div>
          <InputField
            type="email"
            placeholder="이메일을 입력하세요"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            error={errorMsg}
          />

          <Button
            type="submit"
            variant="login-btn"
            size="large"
            disabled={! email}
          >
            확인
          </Button>
        </form>
      </div>
    </div>
  );
}
