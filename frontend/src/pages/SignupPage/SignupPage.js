import { useState } from "react";
import InputField from "../../components/InputField/InputField";
import Button from "../../components/Button/Button";
import "./SignupPage.css";
import { useNavigate } from "react-router-dom";
import * as authAPI from "../../api/authApi";

export default function SignUpPage() {
    const navigate=useNavigate();

    //입력값 상태
    const [username, setUsername] = useState("");
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const [confirmPw, setConfirmPw] = useState("");

    //상태 표시 용
    const [passwordStatus, setPasswordStatus] = useState("default");

    // 에러 상태
    const [usernameError, setUsernameError] = useState("");
    const [emailError, setEmailError] = useState("");
    const [confirmPwError, setConfirmPwError] = useState("");
    const [passwordError, setPasswordError]=useState("");

    //비밀번호 규칙
    const validatePassword=(pw)=>{
        const regex = /^(?=.*[A-Za-z])(?=.*\d)(?=.*[~!@#$%^&*+])[A-Za-z\d~!@#$%^&*+]{8,20}$/;
        return regex.test(pw);
    }
  

    const handleSubmit = async (e) => {
        e.preventDefault();
        let valid = true;

        // 기본 프론트 유효성 검사
        if (!username.trim()) {
        setUsernameError("아이디를 입력하세요");
        valid = false;
        } else {
        setUsernameError("");
        }

        if (!email.trim()) {
        setEmailError("이메일을 입력하세요");
        valid = false;
        } else {
        setEmailError("");
        }

        if(!validatePassword(password)){
            setPasswordError("비밀번호는 8~20자, 영문/숫자/특수문자(~!@#$%^&*+)를 모두 포함해야 합니다. ");
            valid=false;
         }else setPasswordError("");

        // 비밀번호 불일치
        if (password !== confirmPw) {
        setConfirmPwError("비밀번호가 일치하지 않습니다.");
        valid = false;
        } else {
        setConfirmPwError("");
        }

        if(!valid) return;

        try{
            const res=await authAPI.signup({
                username,
                email,
                password,
                passwordConfirm: confirmPw,
                agreeTerms:true,
            });
            navigate("/signup/email",{state: {username, email}});
        }catch(err){
            const msg=err.message;
            if(msg.includes("아이디")) setUsernameError(msg);
            else if(msg.includes("이메일")) setEmailError(msg);
            else alert(msg);
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
                required
                error={usernameError}
            />
            <InputField
                label="이메일"
                placeholder="이메일을 입력해 주세요."
                value={email}
                onChange={(e) => setEmail(e.target.value)}
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
                onChange={(e) => {
                    const value=e.target.value;
                    setConfirmPw(value);
                    if(password!==value){
                        setConfirmPwError("비밀번호가 일치하지 않습니다.");
                    }else{
                        setConfirmPwError("");
                    }
                    }
                }
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
