import { dummyBooks } from "../mocks/dummyBooks";
import { dummyReviews } from "../mocks/dummyReviews";

// 테스트용 로그인
export async function login(username, password) {
  await new Promise((r) => setTimeout(r, 500));

  if (username !== "testuser") {
    return { 
      success: false, 
      error: "USER_NOT_FOUND", 
      message: "존재하지 않는 아이디입니다." 
    };
  }

  // 비밀번호 틀림
  if (password !== "1234") {
    return { 
      success: false, 
      error: "INVALID_PASSWORD",
      message: "비밀번호가 틀렸습니다." 
    };
  }


  return { 
      success: true,
      tokenType: "Bearer",
      accessToken: "mock-access-token",
      refreshToken: "mock-refresh-token",
      expiresIn: 3600, 
      user: { username: "testuser", nickname: "빙봉", email: "test@email.com",
        emailVerified: true, provider: "LOCAL", status: "ACTIVE", createdAt: "2025-01-01T12:00:00Z",
       } 
  };
}

//내 계정 조회 (임시)
export async function getMyAccount() {
  await new Promise((r) => setTimeout(r, 400));

  return {
    success: true,
    account: {
      username: "testuser",
      email: "test@email.com",
      nickname: "빙봉",
      emailVerified: true,
      provider: "LOCAL",
      status: "ACTIVE",
      createdAt: "2025-01-01T12:00:00Z",
    },
  };
}

//내 프로필 조회 (임시)
export async function getMyProfile({ include } = {}) {
  await new Promise((r) => setTimeout(r, 400));

  // dummy user "testuser" 기준
  const username = "testuser";

  const baseProfile = {
    username,
    nickname: "빙봉",
    intro: "나를 소개할 수 있는 한 문장을 적어보세요.",
    profileImageUrl: "",
    backgroundImageUrl: "",
    followersCount: 10,
    followingsCount: 5,
    readBooksCount: dummyBooks.length,

    // 본인 전용 정보
    emailVerified: true,
    provider: "LOCAL",
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
    success: true,
    profile: {
      ...baseProfile,
      stars,
      recentReviews,
    },
  };
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
    success: true,
    profile: {
      ...baseProfile,
      stars,
      recentReviews,
    },
  };
}

// 유저 정보 가져오기
export async function getUserInfo() {
  await new Promise((r) => setTimeout(r, 500));
  return {
    success: true,
    user: {
      id: "testuser",
      nickname: "빙봉",
      intro: "나를 소개할 수 있는 한 문장을 적어보세요.",
      profileImg: "",
      backgroundColor: "#D2E8CD",
      email: "test@email.com",
    },
  };
}

// 프로필 수정
export async function updateProfile(profileData) {
  await new Promise((r) => setTimeout(r, 500));
  // mock: 입력받은 데이터 그대로 반환
  return { success: true, user: { ...profileData } };
}

// 테스트용 비밀번호 검증 api
export async function verifyPassword(userId, currentPassword) {
  console.log("Mock verifyPassword 호출", { userId, currentPassword });
  await new Promise((r) => setTimeout(r, 700));

  // 현재 비밀번호가 "1234"인 경우만 성공 처리
  if (currentPassword === "1234") {
    return { success: true };
  } else {
    return { success: false, message: "현재 비밀번호가 일치하지 않습니다." };
  }
}

// 새 비밀번호 변경
export async function changePassword(userId, newPassword) {
  console.log("📡 Mock changePassword 호출", { userId, newPassword });
  await new Promise((r) => setTimeout(r, 700));

  return { success: true, message: "비밀번호가 성공적으로 변경되었습니다." };
}
