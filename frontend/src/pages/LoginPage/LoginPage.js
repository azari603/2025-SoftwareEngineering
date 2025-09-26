import React, { useState } from "react";
import InputField from "../../components/InputField/InputField";
import Button from "../../components/Button/Button";
import "./LoginPage.css";
import logo from "../../assets/logo.png"
import { Link } from "react-router-dom";
import google_logo from "../../assets/google_logo.png"
import naver_logo from "../../assets/naver_logo.png"
import kakao_logo from "../../assets/kakao_logo.png"

export default function LoginPage() {
    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");

    // 에러 상태
    const [usernameError,setUsernameError] =useState("");
    const [passwordError,setPasswordError] =useState("");


    const handleLogin = (e) => {
        e.preventDefault();
        
        //예시  등록된 계정 testuser/1234
        if(username.trim() !== "testuser"){
            setUsernameError("존재하지 않는 아이디입니다.");
            setPasswordError("");  //아이디 틀리면 비밀번호 에러 초기화
            return;
        }else{
            setUsernameError("");
        }
        if(password !=="1234"){
            setPasswordError("비밀번호가 틀렸습니다.");
        }else{
            setPasswordError("");
        }

        //에러 없을 경우 로그인 성공
        if(username.trim()==="testuser" && password ==="1234"){
            console.log("로그인 성공");
        }
    };

    return (
        <div className="login-container">
        <div className="login-box">
        <Link to="/" className="logo">
          <img src={logo} alt="logo" className="logo-icon" />
          <span className="logo-text">CHAECK</span>
        </Link>

        <h2 className="login-title">로그인</h2>

        {/* 입력폼 */}
        <form onSubmit={handleLogin}>
          <InputField
            type="text"
            placeholder="아이디를 입력하세요"
            value={username}
            onChange={(e) => setUsername(e.target.value)}
            error={usernameError}
            quiredre
          />
          <InputField
            type="password"
            placeholder="비밀번호를 입력하세요"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            error={passwordError}
            required
          />

          {/* 로그인 버튼 */}
          <Button
            type="submit"
            variant="login-btn"
            size="large"
            disabled={!(username && password)}
          >
            로그인
          </Button>
        </form>

        {/* 아이디/비번/회원가입 링크 */}
        <div className="sub-links">
          <button type="button">아이디 찾기</button>
          <span>|</span>
          <button type="button">비밀번호 찾기</button>
          <span>|</span>
          <button type="button">회원가입</button>
        </div>

        {/* SNS 계정 로그인 */}
        <div className="sns-section">
          <p>SNS 계정으로 로그인/가입</p>
          <div className="sns-icons">
            <img src={naver_logo} alt="naver login" />
            <img src={kakao_logo} alt="kakao login" />
            <img src={google_logo} alt="google login" />
          </div>
        </div>
      </div>
    </div>
  );
}
