import { Link } from "react-router-dom";
import "./Button.css"
export default function Button({
  children,
  variant, // 버튼 스타일
  size,     // 버튼 크기
  to,                  // 이동할 경로
  onClick,             
  type = "button",
  disabled = false,
}) {
  // case 1: 라우터 이동용 버튼
  if (to) {
    return (
      <Link to={to} className={`btn ${variant} ${size}`}>
        {children}
      </Link>
    );
  }

  // case 2: 일반 동작 버튼
  return (
    <button
      className={`btn ${variant} ${size}`}
      type={type}
      disabled={disabled}
      onClick={onClick}
    >
      {children}
    </button>
  );
}
