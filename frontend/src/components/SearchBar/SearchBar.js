import "./SearchBar.css";
import searchIcon from "../../assets/search.png"

const SearchBar = ({ placeholder = "책 제목 또는 저자명을 검색해 보세요", variant}) => {
  return (
    <div className={`search-bar ${variant}`}>
      <input
        type="text"
        className="search-input"
        placeholder={placeholder}
      />
      <img src={searchIcon} alt="searchIcon" className="search-icon" />
    </div>
  );
};

export default SearchBar;