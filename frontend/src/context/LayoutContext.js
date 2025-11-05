import { createContext, useState } from "react";

// Context 생성
export const LayoutContext = createContext();

export function LayoutProvider({ children }) {
  // footer 배경색을 전역 상태로 관리
  const [footerColor, setFooterColor] = useState("#FFFFFF"); // 기본값: 흰색

  return (
    <LayoutContext.Provider value={{ footerColor, setFooterColor }}>
      {children}
    </LayoutContext.Provider>
  );
}
