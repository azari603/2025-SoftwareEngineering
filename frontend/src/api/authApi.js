import { dummyBooks } from "../mocks/dummyBooks";
import { dummyReviews } from "../mocks/dummyReviews";
import axiosInstance from "./axiosInstance";

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
  }catch(error){
    if(error.response){
      const errorCode=error.response.data?.errorCode;
      switch (errorCode){
        case "DUBLICATE_USERNAME":
          throw new Error("이미 사용 중인 아이디입니다.");
        case "DUPLICATE_EMAIL":
          throw new Error("이미 등록된 이메일입니다.");
        case "VALIDATION_ERROR":
          throw new Error("입력 값이 올바르지 않습니다.");
        default:
          throw new Error("회원가입 중 오류가 발생했습니다.");
      }
    }
    throw error;
  }
}
// 로그인
export async function login(username, password) {
  try{
    const res=await axiosInstance.post("/auth/login",{
    username,
    password,
  });
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

//내 계정 조회 (임시)
export async function getMyAccount() {
  await new Promise((r) => setTimeout(r, 400));

  return {
    account: {
      username: "testuser",
      email: "test@email.com",
      nickname: "수진",
      emailVerified: true,
      provider: "LOCAL",
      status: "ACTIVE",
      createdAt: "2025-01-01T12:00:00Z",
    },
  };
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

// (타인) 프로필 조회 (임시)
export async function getProfile(username, { include } = {}) {
  await new Promise((r) => setTimeout(r, 500));

  const baseProfile = {
    username,
    nickname: "빙봉",
    intro: "타인의 프로필입니다.",
    profileImageUrl: "",
    backgroundImageUrl: "",
    followersCount: 3,
    followingsCount: 1,
    readBooksCount: dummyBooks.length,
  };

  let stars, recentReviews;

  if (include?.includes("reviews")) {
    recentReviews = dummyReviews
      .filter((rev) => rev.user.id === username)
      .slice(0, 5);
  }

  if (include?.includes("stars")) {
    stars = dummyReviews.reduce((acc, rev) => {
      const r = Math.round(rev.rating);
      acc[r] = (acc[r] || 0) + 1;
      return acc;
    }, {});
  }

  return {
    profile: {
      ...baseProfile,
      stars,
      recentReviews,
    },
  };
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
