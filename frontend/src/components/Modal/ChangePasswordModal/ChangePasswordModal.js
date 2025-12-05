import React, { useState } from "react";
import "./ChangePasswordModal.css";
import { changePassword } from "../../../api/authApi";
import InputField from "../../InputField/InputField";

export default function ChangePasswordModal({ onClose }) {
  const validatePassword = (pw) => {
    const regex = /^(?=.*[A-Za-z])(?=.*\d)(?=.*[~!@#$%^&*+])[A-Za-z\d~!@#$%^&*+]{8,20}$/;
    return regex.test(pw);
  };

  const [currentPw, setCurrentPw] = useState("");
  const [newPw, setNewPw] = useState("");
  const [confirmPw, setConfirmPw] = useState("");

  const [currentPwError, setCurrentPwError] = useState("");
  const [passwordError, setPasswordError] = useState("");
  const [confirmPwError, setConfirmPwError] = useState("");
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (e) => {
    e.preventDefault();

    setCurrentPwError("");
    setPasswordError("");
    setConfirmPwError("");

    // 필수값 체크
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

    try {
      setLoading(true);

      const res = await changePassword(currentPw, newPw);

      if (res.success) {
        alert(res.message);
        onClose();
      } else {
        if (res.code === "INVALID_PASSWORD") {
          setCurrentPwError("현재 비밀번호가 일치하지 않습니다.");
        } else {
          alert("서버 오류가 발생했습니다.");
        }
      }
    } catch (err) {
      console.error(err);
      alert("서버 연결에 실패했습니다.");
    } finally {
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
