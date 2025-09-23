import React from "react";
import { Link, useLocation } from "react-router-dom"; // 현재 경로 확인용
import "./Sidebar.css"; // CSS 불러오기

const Sidebar = () => {
  const location = useLocation(); // 현재 URL 경로 가져오기

  return (
    <aside className="sidebar">
      <ul>
        <li>
          <Link
            to="/library"
            className={location.pathname === "/library" ? "active" : ""}
          >
            나의 서재
          </Link>
        </li>
        <li>
          <Link
            to="/reviews"
            className={location.pathname === "/reviews" ? "active" : ""}
          >
            나의 서평
          </Link>
        </li>
        <li>
          <Link
            to="/stats"
            className={location.pathname === "/stats" ? "active" : ""}
          >
            독서 통계
          </Link>
        </li>
      </ul>
    </aside>
  );
};

export default Sidebar;
