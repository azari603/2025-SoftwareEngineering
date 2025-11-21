import { createContext, useState, useContext, useEffect } from 'react';
import * as authAPI from "../api/authApi"


const AuthContext = createContext(null);

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null);   
  const [accessToken, setAccessToken]=useState(null);
  const [refreshToken, setRefreshToken]=useState(null);
  const isLoggedIn=!!accessToken;

  //앱 시작 시 토큰 복구 -> 새로고침해도 안 날아가게
  useEffect(()=>{
    const savedAcess=localStorage.getItem("accessToken");
    const savedRefresh=localStorage.getItem("refreshToken");
    if(!savedAcess||!savedRefresh) return;
    setAccessToken(savedAcess);
    setRefreshToken(savedRefresh);

    // 계정 조회 호출  -- /auth/me
    authAPI.getMyAccount().then((res)=>{
      if(res?.account){
        setUser(res.account);
      } else{
        logout(); //토큰 만료, 계정 없음 등의 경우 로그아웃
      }
    })
  },[]);

  // 로그인
  const login=async(username, password)=>{
    const res=await authAPI.login(username, password);
    if(res.error){
      return res;
    }

    //로그인 성공
    const {
      accessToken: newAccessToken,
      refreshToken: newRefreshToken,
      user: userData,
    }=res;

    //토큰 저장
    localStorage.setItem("accessToken",newAccessToken);
    localStorage.setItem("refreshToken",newRefreshToken);

    setAccessToken(newAccessToken);
    setRefreshToken(newRefreshToken);
    setUser(userData);

    return {success: true};
  };

  // 로그아웃 함수
  const logout = () => {
    localStorage.removeItem("accessToken");
    localStorage.removeItem("refreshToken");

    setAccessToken(null);
    setRefreshToken(null);
    setUser(null);
  };

  const value = { 
    isLoggedIn, 
    user, 
    accessToken,
    refreshToken,
    setUser,
    login, 
    logout 
  };

  return (
    <AuthContext.Provider value={value}>
      {children}
    </AuthContext.Provider>
  );
};

// 3. Custom Hook
export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth는 AuthProvider 내에서 사용되어야 합니다.');
  }
  return context;
};