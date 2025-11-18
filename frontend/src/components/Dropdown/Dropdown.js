import React, { useState } from "react";
import { IoIosArrowDown } from "react-icons/io";

import "./Dropdown.css";

const Dropdown = ({ value, onChange, options = [] }) => {
  const [open, setOpen] = useState(false);

  return (
    <div className="dropdown-wrapper">
      <button className={`dropdown-selected ${open ? "open" : ""}`} onClick={() => setOpen(!open)}>
        <span>{value}</span>
        <IoIosArrowDown className="dropdown-arrow" size={18} />
      </button>

      {open && (
        <ul className="dropdown-menu">
          {options.map((opt) => (
            <li
              key={opt}
              className="dropdown-item"
              onClick={() => {
                onChange(opt);
                setOpen(false);
              }}
            >
              {opt}
            </li>
          ))}
        </ul>
      )}
    </div>
  );
};

export default Dropdown;
