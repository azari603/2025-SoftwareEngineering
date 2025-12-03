import { useState } from "react";
import "./DeleteAccountModal.css";
import { deleteAccount } from "../../../api/authApi";
import { useAuth } from "../../../context/AuthContext";
import { useNavigate } from "react-router-dom";
import bearImg from "../../../assets/bear_sad.png"; // 곰돌이 이미지 경로에 맞게 수정

export default function DeleteAccountModal({ onClose }) {
  const [password, setPassword] = useState("");
  const [errorMsg, setErrorMsg] = useState("");

  const { logout } = useAuth();
  const navigate = useNavigate();

  const handleDelete = async (e) => {
    e.preventDefault();
    setErrorMsg("");

    if (!password) {
      setErrorMsg("비밀번호를 입력해주세요.");
      return;
    }

    const res = await deleteAccount(password);

    if (res.success) {
      alert("계정이 삭제되었습니다.");
      logout();
      navigate("/login");
    } else {
      if (res.code === "INVALID_PASSWORD") {
        setErrorMsg("비밀번호가 일치하지 않습니다.");
      } else {
        alert("계정 삭제 중 오류가 발생했습니다.");
      }
    }
  };

  return (
    <div className="delete-overlay">
      <div className="delete-modal">

        {/* 곰돌이 이미지 */}
        <img src={bearImg} alt="sad bear" className="delete-bear" />

        <h2 className="delete-title">정말 탈퇴하시겠습니까?</h2>

        <p className="delete-desc">
          탈퇴 버튼 선택 시, 계정은 <br />
          삭제되며 복구되지 않습니다.
        </p>

        <form onSubmit={handleDelete} className="delete-form">
          <label className="delete-label">현재 비밀번호</label>

          <input
            type="password"
            className="delete-input"
            placeholder="비밀번호를 입력해 주세요."
            value={password}
            onChange={(e) => setPassword(e.target.value)}
          />

          {errorMsg && <p className="delete-error">{errorMsg}</p>}

          <div className="delete-buttons">
            <button type="button" className="btn-cancel" onClick={onClose}>
              취소
            </button>
            <button type="submit" className="btn-delete">
              계정 탈퇴
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}
