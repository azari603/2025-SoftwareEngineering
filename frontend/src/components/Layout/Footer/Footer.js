import { FaGithub } from "react-icons/fa6";
import logo from "../../../assets/logo.png"
import "./Footer.css"

export default function Footer({bgColor="#FFFFFF"}){
    return(
        <footer className="footer" style={{backgroundColor:bgColor}}>
            <div className="footer-top">
                <div className="footer-logo">
                    <img src={logo} alt="logo" className="logo-icon" />
                    <span className="logo-text">CHAECK</span>
                </div>
        
                <div className="footer-project">
                    <p>2025-SE Team Project</p>
                    <p>Developted by 이현승 정도희 박정호 이수진 송서현</p>
                    <a
                        href="https://github.com/azari603/2025-SoftwareEngineering"
                        target="_blank"
                        rel="noopener noreferrer"
                        className="github-icon"
                    >
                    <FaGithub size={28}/>
                    </a>
                </div>
            </div>

            <div className="footer-bottom">
                <div className="footer-links">
                    <a href="/">이용약관</a>
                    <a href="/"><strong>개인정보처리방침</strong></a>
                </div>
                <p className="footer-copy">© 2025 CHAECK. All Rights Reserved.</p>
            </div>

        </footer>
    )
}