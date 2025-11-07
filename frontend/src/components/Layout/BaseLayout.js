import { useContext } from "react";
import { Outlet } from "react-router-dom";
import Header from "./Header/Header";
import Footer from "./Footer/Footer";
import ProfileMenu from "../ProfileMenu/ProfileMenu";
import { LayoutContext } from "../../context/LayoutContext";

const BaseLayout=({children})=>{
    const { footerColor } = useContext(LayoutContext);
    return(
        <div className="base-layout">
            <Header/>
            <ProfileMenu />
            <main className="main-container">
                <Outlet />
            </main>
            <Footer bgColor={footerColor}/>
        </div>
    )
}

export default BaseLayout;