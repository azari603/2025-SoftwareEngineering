import { useNavigate } from "react-router-dom";
import "./LoginModal/LoginModal.css"; // 기존 디자인 그대로 사용

const GenericModal = ({
  message,           // 모달 안에 표시할 문구
  confirmText,       // 오른쪽 버튼 텍스트
  cancelText,        // 왼쪽 버튼 텍스트
  onConfirm,         // 오른쪽 버튼 클릭 시 동작
  onCancel,          // 왼쪽 버튼 클릭 시 동작
  showConfirm = true, // 확인 버튼이 필요한지 여부
  navigateTo,        // 특정 경로로 이동 (선택)
}) => {
  const navigate = useNavigate();

  const handleConfirm = () => {
    if (navigateTo) navigate(navigateTo);
    if (onConfirm) onConfirm();
  };

  return (
    <div className="login-modal">
      <div className="login-modal__overlay" onClick={onCancel} />
      <div className="login-modal__content">
        <h3>{message}</h3>
        <div className="login-modal__buttons">
          {cancelText && (
            <button className="cancel-btn" onClick={onCancel}>
              {cancelText}
            </button>
          )}
          {showConfirm && (
            <button className="login-btn" onClick={handleConfirm}>
              {confirmText}
            </button>
          )}
        </div>
      </div>
    </div>
  );
};

export default GenericModal;
