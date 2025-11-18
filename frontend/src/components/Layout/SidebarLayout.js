import { Outlet } from "react-router-dom";
import Header from "./Header/Header";
import Sidebar from "./Sidebar/Sidebar"
import "./SidebarLayout.css"

const SidebarLayout=({children})=>{
    return (
        <div className="sidebar-layout">
            <Header/>
            <div className="main-container">
                <Sidebar/>
                <main className="content">
                    <Outlet/>
                </main>
            </div>
        </div>
    )
}

export default SidebarLayout