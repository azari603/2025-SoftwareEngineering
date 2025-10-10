import React, { useState } from "react";
import "./ChangePasswordModal.css";
import { verifyPassword,changePassword } from "../../../api/authApi";
import { useAuth } from "../../../context/AuthContext";


export default function ChangePasswordModal({ onClose }) {

  // 비밀번호 규칙 검증 함수
  const validatePassword = (pw) => {
    const regex = /^(?=.*[A-Za-z])(?=.*\d)(?=.*[~!@#$%^&*+])[A-Za-z\d~!@#$%^&*+]{8,20}$/;
    return regex.test(pw);
  };

  const {user}=useAuth();
  const [currentPw, setCurrentPw] = useState("");
  const [newPw, setNewPw] = useState("");
  const [confirmPw, setConfirmPw] = useState("");

  const [currentPwError,setCurrentPwError]=useState("");
  const [passwordError, setPasswordError] = useState("");
  const [confirmPwError, setConfirmPwError] = useState("");
  const [loading, setLoading] = useState(false);


  const handleSubmit = async (e) => {
    e.preventDefault();
    setCurrentPwError("");
    setPasswordError("");
    setConfirmPwError("");

    if (!currentPw || !newPw || !confirmPw) {
      alert("모든 항목을 입력해주세요.");
      return;
    }

    if (!validatePassword(newPw)) {
      setPasswordError(
        "비밀번호는 8~20자, 영문/숫자/특수문자(~!@#$%^&*+)를 모두 포함해야 합니다."
      );
      return;
    }

    if (newPw !== confirmPw) {
      setConfirmPwError("비밀번호가 일치하지 않습니다.");
      return;
    }

    try{
      setLoading(true);

      //현재 비밀번호 확인 api 호출
      const verifyRes=await verifyPassword(user?.id,currentPw);
      if(!verifyRes.success){
        setCurrentPwError(verifyRes.message);
        setLoading(false);
        return;
      }
      
      //새 비밀번호 변경 api 호출
      const changeRes=await changePassword(user?.id,newPw);
      if(changeRes.success){
        alert(changeRes.message);
        onClose();
      }else{
        alert("비밀번호 변경에 실패했습니다.")
      }
    } catch(err){
      console.error("Error:",err);
      alert("서버 연결에 실패했습니다.");
    }finally{
      setLoading(false);
    }
  };



  return (
    <div className="modal-overlay">
      <div className="modal-box">
        <h2 className="modal-title">비밀번호 변경</h2>

        <form onSubmit={handleSubmit} className="modal-form">
          <div className="input-group">
            <label>
              현재 비밀번호 <span className="required">*</span>
            </label>
            <input
              type="password"
              placeholder="현재 비밀번호를 입력해 주세요."
              value={currentPw}
              onChange={(e) => setCurrentPw(e.target.value)}
              required
            />
            {currentPwError && <p className="error-msg">{currentPwError}</p>}
          </div>

          <div className="input-group">
            <label>
              새 비밀번호 <span className="required">*</span>
            </label>
            <input
              type="password"
              placeholder="새 비밀번호를 입력해 주세요."
              value={newPw}
              onChange={(e) => {
                const value=e.target.value;
              setNewPw(value);
              if (!validatePassword(value)) {
                setPasswordError(
                  "비밀번호는 8~20자, 영문/숫자/특수문자(~!@#$%^&*+)를 모두 포함해야 합니다."
                );
              }else{
                setPasswordError("");
              }
              }}
              required
            />
            {passwordError && <p className="error-msg">{passwordError}</p>}
          </div>

          <div className="input-group">
            <label>
              비밀번호 확인 <span className="required">*</span>
            </label>
            <input
              type="password"
              placeholder="새 비밀번호를 다시 입력해 주세요."
              value={confirmPw}
              onChange={(e) => setConfirmPw(e.target.value)}
              required 
            />
            {confirmPwError && <p className="error-msg">{confirmPwError}</p>}
          </div>

          <div className="modal-buttons">
            <button type="button" className="cancel-btn" onClick={onClose}>
              취소
            </button>
            <button type="submit" className="submit-btn" disabled={loading}>
                {loading ? "처리중..." : "변경하기"}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}
