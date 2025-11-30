import { useContext } from "react";
import { Outlet, useLocation } from "react-router-dom";
import Header from "./Header/Header";
import Footer from "./Footer/Footer";
import ProfileMenu from "../ProfileMenu/ProfileMenu";
import { LayoutContext } from "../../context/LayoutContext";

const BaseLayout=({children})=>{
    const location = useLocation();
    const isHome = location.pathname === "/";
    const { footerColor } = useContext(LayoutContext);
    return(
        <div className="base-layout">
            <Header isTransparent={isHome}/>
            <ProfileMenu />
            <main className="main-container">
                <Outlet />
            </main>
            <Footer isTransparent={isHome} bgColor={footerColor}/>
        </div>
    )
}

export default BaseLayout;