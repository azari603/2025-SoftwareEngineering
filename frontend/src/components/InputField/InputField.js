// InputField.jsx
import React from "react";
import "./InputField.css";

const InputField = ({ 
  label, 
  type = "text", 
  placeholder, 
  required = false, 
  value, 
  onChange 
}) => {
  return (
    <div className="input-field">
      {label && ( //label이 있을 경우만 렌더링
        <label>
          {label} {required && <span className="required">*</span>}
        </label>
      )}
      
      <input
        type={type}
        placeholder={placeholder}
        value={value}
        onChange={onChange}
        required={required}
      />
    </div>
  );
};

export default InputField;
