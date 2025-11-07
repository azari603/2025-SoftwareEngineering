import { Children } from "react";
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
                    {children}
                </main>
            </div>
        </div>
    )
}

export default SidebarLayout