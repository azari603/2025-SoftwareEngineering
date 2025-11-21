import {useEffect,useState} from "react";
import "./SettingsPage.css";
import profile_img from "../../../assets/profile_img.png";
import { useAuth } from "../../../context/AuthContext";
import { useNavigate } from "react-router-dom";
import ChangePasswordModal from "../../../components/Modal/ChangePasswordModal/ChangePasswordModal.js";


export default function SettingsPage() {
  const { isLoggedIn, user } = useAuth();
  const navigate=useNavigate();
  const [showPasswordModal, setShowPasswordModal] = useState(false);

  

  // 로그인 안 되어 있거나 user 데이터 없을 때
  useEffect(() => {
    if (!isLoggedIn || !user) {
      navigate("/login");
    }
  }, [isLoggedIn, user, navigate]);

  // 기본값 설정
  const profileImage = user?.profileImg || profile_img;
  const nickname = user?.nickname || "사용자";
  const intro = user?.intro || "나를 소개할 수 있는 한 문장을 적어보세요.";
  const username = user?.id || "unknown";
  const email = user?.email || "unknown@test.com";

  return (
    <div className="settings-container">
      <div className="settings-title-wrapper">
        <h2 className="settings-title">계정 설정</h2>
      </div>
      

      {/* 내 프로필 */}
      <div className="settings-card profile-card">
        <div className="settings-header">
          <h3>내 프로필</h3>
          <button className="settings-edit-btn" onClick={()=>navigate("/profile/settings/edit")}>설정</button>
        </div>

        <div className="profile-section">
          {/* 프로필 이미지 */}
          <div className="profile-item">
            <p className="label">프로필</p>
            <div className="value">
              <img src={profileImage} alt="프로필" className="profile-thumb" />
            </div>
          </div>

          {/* 닉네임 */}
          <div className="profile-item">
            <p className="label">닉네임</p>
            <p className="value">{nickname}</p>
          </div>

          {/* 소개 */}
          <div className="profile-item">
            <p className="label">소개</p>
            <p className="value">{intro}</p>
          </div>

          {/* 배경 이미지 */}
          <div className="profile-item">
            <p className="label">배경 이미지</p>
            <div
              className="background-preview"
              style={{
                backgroundImage: user?.backgroundImg
                  ? `url(${user.backgroundImg})`
                  : "none",
                backgroundColor: user?.backgroundColor || "#D2E8CD",
                backgroundSize: "cover",
                backgroundPosition: "center",
              }}
            ></div>
          </div>

        </div>
      </div>

      {/* 기본 정보 */}
      <div className="settings-card info-card">
        <h3>기본 정보</h3>

        <div className="info-section">
          <div className="info-row">
            <p className="label">아이디</p>
            <p className="value">@{username}</p>
          </div>

          <div className="info-row">
            <p className="label">이메일</p>
            <p className="value">{email}</p>
            <button className="small-btn">설정</button>
          </div>

          <div className="info-row">
            <p className="label">비밀번호</p>
            <button className="small-btn" onClick={()=> setShowPasswordModal(true)}>비밀번호 변경</button>
          </div>
        </div>
      </div>

      <div className="delete-btn-wrapper">
        <button className="delete-btn">계정 삭제</button>
      </div>
      {showPasswordModal && (
        <ChangePasswordModal onClose={() => setShowPasswordModal(false)} />
      )}
    </div>
  );
}

