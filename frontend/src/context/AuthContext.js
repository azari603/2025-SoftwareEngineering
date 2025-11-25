import { createContext, useState, useContext, useEffect } from 'react';
import * as authAPI from "../api/authApi"
import axiosInstance from '../api/axiosInstance';


const AuthContext = createContext(null);

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null);   
  const [accessToken, setAccessToken]=useState(
    localStorage.getItem("accessToken")
  );
  const [refreshToken, setRefreshToken]=useState(
    localStorage.getItem("refreshToken")
  );
  const isLoggedIn=!!accessToken;

  //앱 시작 시 토큰 복구 -> 새로고침해도 안 날아가게
  useEffect(()=>{
    if(!accessToken) return;
    fetchMyInfo();
  },[accessToken]);

  const fetchMyInfo=async()=>{
    try{
      const res=await axiosInstance.get("/auth/me");
      setUser(res.data);
    }catch(err){
      console.log("유저 정보 불러오기 실패:",err);
    }
  };

  // 로그인
  const login=async(username, password)=>{
    const res=await authAPI.login(username, password);

    if(!res.ok){
      return res; //{ok: false, code, message}
    }
    const {accessToken, refreshToken}=res.data.data;
    localStorage.setItem("accessToken",accessToken);
    localStorage.setItem("refreshToken", refreshToken);

    setAccessToken(accessToken);
    setRefreshToken(refreshToken);
    await fetchMyInfo();

    return {ok: true};
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