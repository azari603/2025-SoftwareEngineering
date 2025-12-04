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
    const fields=err.response?.data?.fields;
    return {
      ok: false,
      code,
      message,
      fields,
    };
  }
  
}

//로그아웃
export async function logout() {
  try {
    const response = await axiosInstance.post("/auth/logout", null, {
      withCredentials: true,  // 쿠키 포함 (HttpOnly refreshToken 삭제 용)
    });

    // 204 No Content → 성공
    if (response.data.success) {
      return { ok: true };
    }

    return { ok: false, message: "로그아웃 실패ㅜㅜ" };
  } catch (error) {
    console.error("Logout failed:", error);
    return { ok: false, message: error.response?.data?.message };
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

//아이디 찾기
export async function findId(email) {
  try {
    const res = await axiosInstance.get("/auth/find-id", {
      params: { email },
    });

    return {
      ok: true,
      data: res.data.data,   // { username: "..." }
    };
  } catch (err) {
    return {
      ok: false,
      code: err.response?.data?.code,
      message: err.response?.data?.message || "아이디 찾기 실패",
    };
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

//별점별 책 목록
export async function getMyStarredBooks(rating, page = 0, size = 20) {
  const res = await axiosInstance.get(
    `/stats/me/stars/books`,
    {
      params: { rating, page, size }
    }
  );
  return res.data; 
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
export async function changePassword(currentPassword, newPassword) {
  try {
    const res = await axiosInstance.post("/auth/password/change", {
      currentPassword,
      newPassword,
    });

    return {
      success: true,
      message: res.data.data || res.data.message, // “비밀번호가 변경되었습니다.”
    };
  } catch (err) {
    const code = err.response?.data?.code;

    return {
      success: false,
      code,
      message:
        code === "INVALID_PASSWORD"
          ? "현재 비밀번호가 일치하지 않습니다."
          : "서버 오류가 발생했습니다.",
    };
  }
}

//계정 탈퇴
export async function deleteAccount(password) {
  try {
    const res = await axiosInstance.delete("/auth/me", {
      data: { password },
    });

    return { success: true };
  } catch (err) {
    const code = err.response?.data?.code;

    return {
      success: false,
      code,
      message:
        code === "INVALID_PASSWORD"
          ? "비밀번호가 일치하지 않습니다."
          : "계정 삭제 중 오류가 발생했습니다."
    };
  }
}

// 알림 목록 조회
export async function getNotifications(page = 0, size = 20) {
  const res = await axiosInstance.get("/notifications", {
    params: { page, size, sort: "createdAt,desc" }
  });

  return res.data?.content || [];
}

// 알림 읽지 않은 개수
export async function getUnreadCount() {
  const res = await axiosInstance.get("/notifications/unread-count");
  return res.data.unreadCount;
}

// 단건 읽음 처리
export async function readNotification(id) {
  await axiosInstance.patch(`/notifications/${id}/read`);
}

//  전체 읽음 처리
export async function readAllNotifications() {
  await axiosInstance.patch("/notifications/read-all");
}

// 단건 삭제
export async function deleteNotification(id) {
  await axiosInstance.delete(`/notifications/${id}`);
}

export async function getUserById(userId) {
  const res = await axiosInstance.get(`/users/${userId}`);
  return res.data; // { id, username, nickname, ... }
}
