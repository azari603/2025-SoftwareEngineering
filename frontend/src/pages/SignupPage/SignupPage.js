import React, { useState } from "react";
import InputField from "../../components/InputField/InputField";
import Button from "../../components/Button/Button";
import "./SignupPage.css";
import { useNavigate } from "react-router-dom";

export default function SignUpPage() {
    const navigate=useNavigate();
    const [username, setUsername] = useState("");
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const [passwordStatus, setPasswordStatus] = useState("default");
    const [confirmPw, setConfirmPw] = useState("");

    // 에러 상태
    const [usernameError, setUsernameError] = useState("");
    const [emailError, setEmailError] = useState("");
    const [confirmPwError, setConfirmPwError] = useState("");
    const [passwordError, setPasswordError]=useState("");

    const validatePassword=(pw)=>{
        const regex = /^(?=.*[A-Za-z])(?=.*\d)(?=.*[~!@#$%^&*+])[A-Za-z\d~!@#$%^&*+]{8,20}$/;
        return regex.test(pw);
    }
  

    const handleSubmit = (e) => {
        e.preventDefault();
        console.log("username",username);
        let valid = true;

        // 아이디 중복 예시
        if (username.trim() === "testuser") {
        setUsernameError("중복된 아이디입니다.");
        valid = false;
        } else {
        setUsernameError("");
        }

        // 이메일 중복 예시
        if (email === "test@email.com") {
        setEmailError("중복된 이메일입니다.");
        valid = false;
        } else {
        setEmailError("");
        }

        // 비밀번호 불일치
        if (password !== confirmPw) {
        setConfirmPwError("비밀번호가 일치하지 않습니다.");
        valid = false;
        } else {
        setConfirmPwError("");
        }

        if (valid) {
        console.log("회원가입 성공", { username, email, password });
        sessionStorage.setItem("username",username);
        sessionStorage.setItem("email",email);
        navigate("/signup/email", { state: { username,email } });
        }
    };

    return (
        <>
            <h2 className="signup-title">회원가입</h2>

            <form onSubmit={handleSubmit}>
            <InputField
                label="아이디"
                placeholder="닉네임을 입력해 주세요."
                value={username}
                onChange={(e) => setUsername(e.target.value)}
                onBlur={() => {
                    if (username.trim() === "testuser") {
                    setUsernameError("중복된 아이디입니다.");
                    } else {
                    setUsernameError("");
                    }
                }}
                required
                error={usernameError}
            />
            <InputField
                label="이메일"
                placeholder="이메일을 입력해 주세요."
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                onBlur={() => {
                    if (email === "test@email.com") {
                    setEmailError("중복된 이메일입니다.");
                    } else {
                    setEmailError("");
                    }
                }}
                required
                error={emailError}
            />
            <InputField
                label="비밀번호"
                type="password"
                placeholder="비밀번호를 입력해 주세요."
                value={password}
                onChange={(e) => {
                    const newPw = e.target.value;
                    setPassword(newPw);

                    if (!validatePassword(newPw)) {
                        setPasswordError("비밀번호는 8~20자, 영문/숫자/특수문자(~!@#$%^&*+)를 모두 포함해야 합니다.");
                        setPasswordStatus("error");
                        } else {
                        setPasswordError("");
                        setPasswordStatus("success");
                        }
                    }}
                    onBlur={() => setPasswordStatus("default")}   // 다른 칸으로 가면 기본 검정
                    required
                    error={passwordError}
                    status={passwordStatus}
            />
            <InputField
                label="비밀번호 확인"
                type="password"
                placeholder="비밀번호를 재입력해 주세요."
                value={confirmPw}
                onChange={(e) => setConfirmPw(e.target.value)}
                onBlur={() => {
                    if (password !== confirmPw) {
                    setConfirmPwError("비밀번호가 일치하지 않습니다.");
                    } else {
                    setConfirmPwError("");
                    }
                }}
                required
                error={confirmPwError}
            />

            <Button type="submit" variant="login-btn" size="large">
                가입하기
            </Button>
        </form>
        </>
    );
}
