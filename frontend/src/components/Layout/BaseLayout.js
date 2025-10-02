import { Children } from "react";
import Header from "./Header/Header";

const BaseLayout=({children})=>{
    return(
        <div className="base-layout">
            <Header/>
            <main className="main-container">
                {children}
            </main>
            
        </div>
    )
}

export default BaseLayout;