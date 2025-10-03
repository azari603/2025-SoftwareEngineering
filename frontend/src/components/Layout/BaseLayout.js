import { Outlet } from "react-router-dom";
import Header from "./Header/Header";
import Footer from "./Footer/Footer";

const BaseLayout=({children})=>{
    return(
        <div className="base-layout">
            <Header/>
            <main className="main-container">
                <Outlet />
            </main>
            <Footer/>
        </div>
    )
}

export default BaseLayout;