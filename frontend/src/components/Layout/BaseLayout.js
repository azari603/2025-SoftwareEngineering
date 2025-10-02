import { Outlet } from "react-router-dom";
import Header from "./Header/Header";

const BaseLayout=({children})=>{
    return(
        <div className="base-layout">
            <Header/>
            <main className="main-container">
                <Outlet />
            </main>
            
        </div>
    )
}

export default BaseLayout;