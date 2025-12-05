import React from "react";
import "./DatePickerModal.css";

const DatePickerModal = ({ onClose, onSelect, min, max }) => {
  const handleChange = (e) => {
    onSelect(e.target.value);
    onClose();
  };

  return (
    <div className="datepicker-overlay">
      <div className="datepicker-box">
        <input
          type="date"
          className="datepicker-input"
          min={min}
          max={max}
          onChange={handleChange}
        />
        <button className="datepicker-close" onClick={onClose}>
          닫기
        </button>
      </div>
    </div>
  );
};

export default DatePickerModal;
