import React, { useState, useRef } from "react";
import "./ProfileEditPage.css";
import profile_img from "../../../assets/profile_img.png";
import { useAuth } from "../../../context/AuthContext";
import { useNavigate } from "react-router-dom";
import camera_img from "../../../../src/assets/camera.png";

export default function ProfileEditPage() {
  const { user, setUser } = useAuth();
  const navigate = useNavigate();

  // 초기값
  const [nickname, setNickname] = useState(user?.nickname || "");
  const [intro, setIntro] = useState(user?.intro || "");
  const [profileImage, setProfileImage] = useState(user?.profileImg || profile_img);
  const [bgImage, setBgImage] = useState(user?.backgroundImg || null);

  const profileInputRef = useRef(null);
  const bgInputRef = useRef(null);

  // 프로필 이미지 변경
  const handleProfileChange = (e) => {
    const file = e.target.files[0];
    if (file) {
      const previewURL = URL.createObjectURL(file);
      setProfileImage(previewURL); // 프리뷰용 URL
    }
  };

  // 배경 이미지 변경
  const handleBgChange = (e) => {
    const file = e.target.files[0];
    if (file) {
      const previewURL = URL.createObjectURL(file);
      setBgImage(previewURL); // 프리뷰용 URL
    }
  };

  // 저장 (지금은 로컬 상태만 업데이트)
  const handleSave = () => {
    const updatedUser = {
      ...user,
      nickname,
      intro,
      profileImg: profileImage,
      backgroundImg: bgImage,
    };

    // 화면 즉시 반영
    setUser(updatedUser);

    alert("프로필이 수정되었습니다.");
    navigate(-1);
  };

  return (
    <div className="edit-container">
      <div className="edit-title-wrapper">
        <h2 className="edit-title">프로필 수정</h2>
      </div>

      <div className="edit-card">
        {/* 프로필 이미지 */}
        <div className="edit-header">
          <p className="label">프로필</p>
          <div
            className="profile-thumb"
            onClick={() => profileInputRef.current.click()}
          >
            <img src={profileImage} alt="프로필 이미지" />
            <div className="camera-overlay">
              <img
                src={camera_img}
                alt="카메라 아이콘"
                className="camera-icon-profile"
              />
            </div>
          </div>
          <input
            type="file"
            accept="image/*"
            ref={profileInputRef}
            onChange={handleProfileChange}
            style={{ display: "none" }}
          />
        </div>

        <div className="edit-section">
          {/* 닉네임 */}
          <div className="edit-item">
            <p className="label">닉네임</p>
            <input
              type="text"
              className="edit-input"
              value={nickname}
              onChange={(e) => setNickname(e.target.value)}
              placeholder="닉네임을 입력하세요"
            />
          </div>

          {/* 소개 */}
          <div className="edit-item">
            <p className="label">소개</p>
            <input
              type="text"
              className="edit-input"
              value={intro}
              onChange={(e) => setIntro(e.target.value)}
              placeholder="소개 문장을 입력하세요"
            />
          </div>

          {/* 배경 이미지 */}
          <div className="edit-item">
            <p className="label">배경 이미지</p>
            <div
              className="background-preview editable"
              onClick={() => bgInputRef.current.click()}
              style={{
                backgroundImage: bgImage ? `url(${bgImage})` : "none",
                backgroundSize: "cover",
                backgroundPosition: "center",
              }}
            >
              <div className="camera-overlay">
                <img
                  src={camera_img}
                  alt="카메라 아이콘"
                  className="camera-icon"
                />
              </div>
            </div>
            <input
              type="file"
              accept="image/*"
              ref={bgInputRef}
              onChange={handleBgChange}
              style={{ display: "none" }}
            />
          </div>
        </div>

        <div className="edit-btn-wrapper">
          <button className="save-btn" onClick={handleSave}>
            저장
          </button>
        </div>
      </div>
    </div>
  );
}
