import { useNavigate } from "react-router-dom";
import "./LoginModal.css";

const LoginModal = ({ onClose }) => {
  const navigate = useNavigate();

  return (
    <div className="login-modal">
      <div className="login-modal__overlay" onClick={onClose} />
      <div className="login-modal__content">
        <h3>로그인 후 사용할 수 있습니다.</h3>
        <div className="login-modal__buttons">
          <button className="cancel-btn" onClick={onClose}>
            확인
          </button>
          <button
            className="login-btn"
            onClick={() => {
              onClose();
              navigate("/login");
            }}
          >
            로그인하러가기
          </button>
        </div>
      </div>
    </div>
  );
};

export default LoginModal;
