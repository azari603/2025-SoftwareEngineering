import React, { useState, useRef, useEffect } from "react";
import "./ProfileEditPage.css";
import profile_img from "../../../assets/profile_img.png";
import { useAuth } from "../../../context/AuthContext";
import { useNavigate } from "react-router-dom";
import camera_img from "../../../../src/assets/camera.png";
import * as authAPI from "../../../api/authApi";

const base=process.env.REACT_APP_BASE_URL;
function fullUrl(path) {
  if (!path) return null;
  if (path.startsWith("http")) return path;
  return `${base}/${path}`; // base 붙이기
}
export default function ProfileEditPage() {
  const { user, setUser } = useAuth();
  const navigate = useNavigate();
  const[profile,setProfile]=useState();

  

  useEffect(()=>{
      async function loadProfiles(){
        try{
          const res=await authAPI.getMyProfile();
          setProfile(res);
        }catch (err) {
          console.error("프로필 불러오기 실패", err);
          alert("프로필 정보를 불러오지 못했습니다.");
        } 
      }
      loadProfiles();
    },[])


  // 초기값
  const [nickname, setNickname] = useState(profile?.nickname || "");
  const [intro, setIntro] = useState(profile?.intro || "");
  const [profileImage, setProfileImage] = useState(profile?.profileImageUrl || profile_img);
  const [bgImage, setBgImage] = useState(profile?.backgroundImageUrl || null);
  const [profileImagePreview, setProfileImagePreview]=useState(profile?.profileImageUrl||profile_img);
  const [bgImagePreview, setBgImagePreview]=useState(profile?.backgroundImageUrl||null);
  const profileInputRef = useRef(null);
  const bgInputRef = useRef(null);

  const [isProfileChanged, setIsProfileChanged] = useState(false);
  const [isBgChanged, setIsBgChanged] = useState(false);

  useEffect(() => {
    if (profile) {
      const base=process.env.REACT_APP_BASE_URL;
      const fullProfileImage = profile.profileImageUrl
      ? (profile.profileImageUrl.startsWith("http")
          ? fullUrl(profile.profileImageUrl)
          : fullUrl(profile.profileImageUrl))
      : profile_img;

    const fullBgImage = profile.backgroundImageUrl
      ? (profile.backgroundImageUrl.startsWith("http")
          ? fullUrl(profile.backgroundImageUrl)
          : fullUrl(profile.backgroundImageUrl))
      : null;

      setNickname(profile.nickname || "");
      setIntro(profile.intro || "");
      setProfileImagePreview(fullProfileImage);
      setBgImagePreview(fullBgImage);
    }
  }, [profile]);

  // 프로필 이미지 변경
  const handleProfileChange = (e) => {
    const file = e.target.files[0];
    if (file) {
      setProfileImage(file);
      const previewURL = URL.createObjectURL(file);
      setProfileImagePreview(previewURL); // 프리뷰용 URL
      setIsProfileChanged(true); 
    }
  };

  // 배경 이미지 변경
  const handleBgChange = (e) => {
    const file = e.target.files[0];
    if (file) {
      setBgImage(file);
      const previewURL = URL.createObjectURL(file);
      setBgImagePreview(previewURL); // 프리뷰용 URL
      setIsBgChanged(true);
    }
  };

  // 저장 (지금은 로컬 상태만 업데이트)
  const handleSave = async () => {
    const updatedUser = { ...user };
    // 닉네임/소개 변경
      if (nickname !== profile.nickname || intro !== profile.intro) {
        const res = await authAPI.updateProfile({ nickname, intro });
        if (!res.success) {
          alert(res.message || "프로필 수정 오류가 발생했습니다.");
          return;
        }
        updatedUser.nickname = nickname;
        updatedUser.intro = intro;
      }
      // 프로필 이미지 업로드
      if (isProfileChanged) {
        const res = await authAPI.uploadProfileImage(profileImage);
        if (!res.success) {
          alert(res.message || "프로필 이미지 업로드 중 오류");
          return;
        }
        updatedUser.profileImg = res.profileImageUrl;
      }
      // 배경 이미지 업로드
      if (isBgChanged) {
        const res = await authAPI.uploadBackgroundImage(bgImage);
        if (!res.success) {
          alert(res.message || "배경 이미지 업로드 중 오류");
          return;
        }
        updatedUser.backgroundImg = res.backgroundImageUrl;
      }
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
            <img src={profileImagePreview}
            onError={(e) => {
            e.target.src = profile_img; // 기본 이미지로 변경
          }}
            alt="프로필 이미지" />
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
                backgroundImage: bgImagePreview ? `url(${bgImagePreview})` : "none",
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
