import { useState } from "react";
import { useAuth } from "../../context/AuthContext";
import InputField from "../../components/InputField/InputField";
import Button from "../../components/Button/Button";
import "./LoginPage.css";
import logo from "../../assets/logo.png"
import { Link,useNavigate } from "react-router-dom";
import google_logo from "../../assets/google_logo.png"
import naver_logo from "../../assets/naver_logo.png"
import kakao_logo from "../../assets/kakao_logo.png"
import CustomModal from "../../components/Modal/CustomModal"
import { resendVerifyEmail } from "../../api/authApi";
export default function LoginPage() {
    const navigate=useNavigate();
    const {login}=useAuth();

    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");

    // 에러 상태
    const [usernameError,setUsernameError] =useState("");
    const [passwordError,setPasswordError] =useState("");
    const [generalError, setGeneralError] = useState("");
    const [showVerifyModal, setShowVerifyModal] = useState(false);
    const [unverifiedEmail, setUnverifiedEmail] = useState("");

    //소셜 로그인
    const handleSocialLogin = (provider) => {
        window.location.href = `http://localhost:8080/oauth2/authorization/${provider}`;
    };

    const handleLogin = async (e) => {
        e.preventDefault();

        setUsernameError("");
        setPasswordError("");
        setGeneralError("");

        const result=await login(username, password);

        if(!result.ok){
          switch(result.code){
            case "USER_NOT_FOUND":
              setUsernameError("존재하지 않는 아이디입니다.");
              break;
            case "INVALID_PASSWORD":
              setPasswordError("비밀번호가 틀렸습니다.");
              break;
            case "EMAIL_NOT_VERIFIED":
              setUnverifiedEmail(result.fields.email);
              setShowVerifyModal(true);
              break;
            case "USER_LOCKED":
              setGeneralError("해당 계정은 잠겨있습니다.");
              break;
            default:
              setGeneralError(result.message||"로그인에 실패하였습니다.");
          }
          return;

        }

        //로그인 성공 -> 홈으로 이동
        navigate("/")
        
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
          <button type="button" onClick={()=>navigate("/find-id")}>아이디 찾기</button>
          <span>|</span>
          <button type="button" onClick={()=> navigate("/find-password")}>비밀번호 찾기</button>
          <span>|</span>
          <button type="button" variant="outlined" size="small"
          onClick={() => navigate("/signup")}>회원가입</button>
        </div>

        
      </div>
      {showVerifyModal && (
  <CustomModal
    title="이메일 인증 필요"
    message={`로그인을 계속하려면 이메일 인증이 필요합니다. 
      인증 페이지로 이동하시겠습니까?`}
    onConfirm={async () => {
      const result = await resendVerifyEmail(unverifiedEmail);

      if (result.ok) {
        navigate("/signup/email", { state: { email: unverifiedEmail } });
      } else {
        alert("인증 이메일 재전송에 실패했습니다.");
      }
      }
    }
    onCancel={() => setShowVerifyModal(false)}
  />
)}
    </div>
  );
}
