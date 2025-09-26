import React from "react";
import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import LoginPage from "./pages/LoginPage/LoginPage";     
import SignupPage from "./pages/SignupPage/SignupPage"; 
import SignupLayout from "./pages/SignupPage/SignupLayout";
import SignupSuccess from "./pages/SignupPage/SignupSuccess/SignupSuccess";
import SignupEmail from "./pages/SignupPage/SignupEmail/SignupEmail";


function App() {
  return (
    <Router>
      <Routes>

        <Route path="/" element={<LoginPage />} />
        <Route path="/login" element={<LoginPage />} />
        <Route path="/signup" element={<SignupLayout />}>
          <Route index element={<SignupPage />} />
          <Route path="success" element={<SignupSuccess />} />
          <Route path="email" element={<SignupEmail />} />
        </Route>
      </Routes>
    </Router>
  );
}


export default App;
