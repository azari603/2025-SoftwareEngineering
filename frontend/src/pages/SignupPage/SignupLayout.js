// SignupLayout.js
import { Outlet, Link } from "react-router-dom";
import logo from "../../assets/logo.png";
import "./SignupPage.css";

export default function SignupLayout() {
  return (
    <div className="signup-container">
      <div className="signup-box">
        <Link to="/" className="logo">
          <img src={logo} alt="logo" className="logo-icon" />
          <span className="logo-text">CHAECK</span>
        </Link>
        <Outlet /> 
      </div>
    </div>
  );
}
