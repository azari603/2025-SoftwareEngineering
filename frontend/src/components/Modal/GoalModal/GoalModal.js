import { useState } from "react";
import "./GoalModal.css";

const GoalModal = ({ isOpen, onClose, onSubmit }) => {
  const [value, setValue] = useState("");

  if (!isOpen) return null;

  return (
    <div className="goal-overlay" onClick={onClose}>
      <div className="goal-modal" onClick={(e) => e.stopPropagation()}>
        <h3 className="goal-title">이달의 목표 설정</h3>

        <input
          type="number"
          placeholder="목표 권수"
          className="goal-input"
          value={value}
          onChange={(e) => setValue(e.target.value)}
        />

        <button
          className="goal-btn"
          onClick={() => onSubmit(value)}
        >
          완료
        </button>
      </div>
    </div>
  );
};

export default GoalModal;
