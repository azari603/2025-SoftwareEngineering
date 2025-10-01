import { Children } from "react";
import Header from "./Header/Header";

const BaseLayout=({children})=>{
    return(
        <div className="base-layout">
            <Header/>
            <div className="main-container">
                {children}
            </div>
            
        </div>
    )
}

export default BaseLayout;