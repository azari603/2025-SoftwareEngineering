import { Outlet } from "react-router-dom";
import Header from "./Header/Header";
import ProfileMenu from "../ProfileMenu/ProfileMenu";

const BaseLayout=({children})=>{
    return(
        <div className="base-layout">
            <Header/>
            <ProfileMenu />
            <main className="main-container">
                <Outlet />
            </main>
            
        </div>
    )
}

export default BaseLayout;