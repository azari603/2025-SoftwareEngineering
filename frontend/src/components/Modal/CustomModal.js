// CustomModal.js
import "./CustomModal.css";

export default function CustomModal({ title, message, onConfirm, onCancel }) {
  return (
    <div className="modal-backdrop">
      <div className="modal-box">
        <h3 className="modal-title">{title}</h3>
        <p className="modal-message">{message}</p>

        <div className="custom-modal-buttons">
          <button className="modal-btn confirm" onClick={onConfirm}>
            확인
          </button>
          <button className="modal-btn cancel" onClick={onCancel}>
            취소
          </button>
        </div>
      </div>
    </div>
  );
}
