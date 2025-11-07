// InputField.jsx
import React from "react";
import "./InputField.css";

const InputField = ({ 
  label, 
  type = "text", 
  placeholder, 
  required = false, 
  value, 
  onChange,
  onBlur,
  error,
  status,  //error, success, default
}) => {
  let inputClass ="";
  if(status==="error") inputClass="error-input";
  if(status==="success") inputClass="success-input";

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
        onBlur={onBlur}
        required={required}
        className={inputClass}
      />
      {error && <p className="error-message">{error}</p>}
    </div>
  );
};

export default InputField;
