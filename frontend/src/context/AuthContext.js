import { createContext, useState, useContext } from 'react';

// 1. Context 생성
const AuthContext = createContext(null); // 초기값은 null로 설정

// 2. Provider 컴포넌트: 상태와 함수를 제공하는 역할
export const AuthProvider = ({ children }) => {
  const [isLoggedIn, setIsLoggedIn] = useState(false); 
  const [user, setUser] = useState(null); 

  // (임시) 로그인 함수
  const login = (userData = { nickname: "CHAECK User", id: 1 }) => {
    console.log("로그인 처리 완료.");
    setIsLoggedIn(true);
    setUser(userData);
  };

  // (임시) 로그아웃 함수
  const logout = () => {
    console.log("로그아웃 처리 완료.");
    setIsLoggedIn(false);
    setUser(null);
  };

  const value = { 
    isLoggedIn, 
    user, 
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