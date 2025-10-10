// 테스트용 로그인
export async function login(username, password) {
  await new Promise((r) => setTimeout(r, 500));

  if (username === "testuser" && password === "1234") {
    return { success: true, user: { id: "testuser", nickname: "빙봉", email: "test@email.com" } };
  }
  return { success: false, message: "아이디 또는 비밀번호가 올바르지 않습니다." };
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
