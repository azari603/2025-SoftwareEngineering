import "./SearchBar.css";
import searchIcon from "../../assets/search.png"
import {useState} from "react"
import {useNavigate} from "react-router-dom"

const SearchBar = ({ placeholder = "책 제목 또는 저자명을 검색해 보세요", variant}) => {
  const [query, setQuery]=useState("")
  const navigate=useNavigate()

  const handleSubmit=(e)=>{
    e.preventDefault()
    if(!query.trim()) return
    navigate(`/search?query=${encodeURIComponent(query)}`)
  }

  return (
    <form className={`search-bar ${variant}`} onSubmit={handleSubmit}>
      <input
        type="text"
        className="search-input"
        placeholder={placeholder}
        value={query}
        onChange={(e)=>setQuery(e.target.value)}
      />
      <button type="submit" className="search-btn">
        <img src={searchIcon} alt="searchIcon" className="search-icon" />
      </button>
      
    </form>
  );
};

export default SearchBar;