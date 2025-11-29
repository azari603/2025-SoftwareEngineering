import axiosInstance from "./axiosInstance";
const token = localStorage.getItem("accessToken");

//회원가입
export async function signup({username, email, password, passwordConfirm, agreeTerms}){
  try{
    const res=await axiosInstance.post("/auth/signup",{
      username,
      email,
      password,
      passwordConfirm,
      agreeTerms,
    });
    return res.data; //인증 메일 발송
  }catch (error) {
    const errorCode = error.response?.data?.code;

    const errorMap = {
      DUPLICATE_USERNAME: "이미 사용 중인 아이디입니다.",
      DUPLICATE_EMAIL: "이미 등록된 이메일입니다.",
      VALIDATION_ERROR: "입력 값이 올바르지 않습니다.",
    };

    const message = errorMap[errorCode] || "회원가입 중 오류가 발생했습니다.";

    // 코드 + 메시지 함께 throw
    throw { code: errorCode, message };
  }
}
// 로그인
export async function login(username, password) {
  try{
    const res=await axiosInstance.post("/auth/login",{
    username,
    password,
  },{withCredentials: true});
    return {
      ok: true,
      data: res.data,
    };
  }catch(err){
    const code=err.response?.data?.code;
    const message=err.response?.data?.message;
    return {
      ok: false,
      code,
      message,
    };
  }
  
}

//인증 메일 재전송
export async function resendVerifyEmail(email) {
  try {
    const res = await axiosInstance.post("/auth/email/resend", { email });
    return {
      ok: true,
      message: res.data?.message || "인증 이메일을 다시 전송했습니다.",
    };
  } catch (err) {
    return {
      ok: false,
      code: err.response?.data?.code,
      message: err.response?.data?.message || "재전송 중 오류가 발생했습니다.",
    };
  }
}

//이메일 인증 확인
export async function checkEmailVerified(email) {
  try {
    const res = await axiosInstance.get("/auth/email/verified", {
      params: { email },
    });

    return {
      ok: res.data.success,
      verified: res.data.data,  // true면 인증됨, false면 미인증
      message: res.data.message,
    };

  } catch (error) {
    console.error("이메일 인증 확인 실패:", error);

    throw new Error(
      error.response?.data?.message ||
      "이메일 인증 여부를 확인하는 중 오류가 발생했습니다."
    );
  }
}

//내 계정 조회
export async function getMyAccount() {
  try{
    const res=await axiosInstance.get("/auth/me");
    return res.data;
  }catch (err){
    console.error("내 계정 조회 실패",err);
    return {
      ok: false,
      code: err.response?.data?.code,
      message: err.response?.data?.message || "내 계정 조회 중 오류가 발생했습니다.",
    };
  }
}

//내 프로필 조회
export async function getMyProfile(){
  try{
    const res=await axiosInstance.get("/profiles/me");
    return res.data;
  }catch (err){
    console.error("내 프로필 조회 실패",err);
    if(err.response?.data?.error==="USER_NOT_FOUND"){
      throw new Error("사용자를 찾을 수 없습니다");
    }
    throw err;
  }
}

// (타인/본인) 프로필 조회 
export async function getProfile(username, { include =[]} = {}) {
  try{
    const res=await axiosInstance.get(`/profiles/${username}`,{
      params: {
        include: include.join(","),
      },
    });
    return{
      success: true,
      profile: res.data,
    }
  }catch (err) {
    console.error("프로필 조회 실패:", err);

    if (err.response?.data?.code === "USER_NOT_FOUND") {
      return {
        success: false,
        error: "USER_NOT_FOUND",
      };
    }

    return {
      success: false,
      error: err.response?.data || "서버 오류",
    };
  }

}

//닉네임/소개 수정
export async function updateProfile({nickname, intro}){
  try {
      const body = {};
      if (nickname) body.nickname = nickname;
      if (intro) body.intro = intro;

      const res = await axiosInstance.patch("/profiles/me", body);
      return { success: true };
    } catch (err) {
      return {
        success: false,
        code: err.response?.data?.code,
        message: err.response?.data?.message || "프로필 수정 오류",
      };
    }
}

//프로필 이미지 업로드
export async function uploadProfileImage(file){
  try {
      const formData = new FormData();
      formData.append("file", file);

      const res = await axiosInstance.put("/profiles/me/image", formData, {
        headers: { 
          Authorization: `Bearer ${token}`,
          "Content-Type": "multipart/form-data" },
      });

      return {
        success: true,
        profileImageUrl: res.data.profileImageUrl,
      };
    } catch (err) {
      return {
        success: false,
        code: err.response?.data?.code,
        message: err.response?.data?.message || "프로필 이미지 업로드 오류",
      };
    }
}

//배경 이미지 업로드
export async function uploadBackgroundImage(file){
  try {
      const formData = new FormData();
      formData.append("file", file);

      const res = await axiosInstance.put(
        "/profiles/me/background",
        formData,
        {
          headers: { 
            Authorization: `Bearer ${token}`,
            "Content-Type": "multipart/form-data" },
        }
      );

      return {
        success: true,
        backgroundImageUrl: res.data.backgroundImageUrl,
      };
    } catch (err) {
      return {
        success: false,
        code: err.response?.data?.code,
        message: err.response?.data?.message || "배경 이미지 업로드 오류",
      };
    }
}

// 테스트용 비밀번호 검증 api
export async function verifyPassword(username, currentPassword) {
  console.log("Mock verifyPassword 호출", { username, currentPassword });
  await new Promise((r) => setTimeout(r, 700));

  // 현재 비밀번호가 "1234"인 경우만 성공 처리
  if (currentPassword === "1234") {
    return {};
  } else {
    return { success: false, message: "현재 비밀번호가 일치하지 않습니다." };
  }
}

// 새 비밀번호 변경
export async function changePassword(username, newPassword) {
  console.log("📡 Mock changePassword 호출", { username, newPassword });
  await new Promise((r) => setTimeout(r, 700));

  return { message: "비밀번호가 성공적으로 변경되었습니다." };
}
