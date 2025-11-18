import { useEffect, useState } from "react";
import { useLocation } from "react-router-dom";
import StarRate from "../../components/StarRate/StarRate";
import DatePickerModal from "../../components/Modal/DatePickerModal/DatePickerModal";
import Dropdown from "../../components/Dropdown/Dropdown";
import { getBookByISBN } from "../../api/bookAPI";
import { CiCalendar } from "react-icons/ci";
import "./ReviewWrite.css";

const ReviewWrite = () => {
    const location = useLocation();
    const params=new URLSearchParams(location.search);
    const isbn=params.get("bookId");

    const[book, setBooks]=useState(null);
    const[loading, setLoading]=useState(true);

    useEffect(()=>{
        async function fetchBooks(){
            const res=await getBookByISBN(isbn);
            if(res.success){
                setBooks(res.book);
            }
            setLoading(false);
        }
        fetchBooks()
    },[isbn]);
    
    const [rating, setRating] = useState(0);
    const [startDate, setStartDate] = useState("");
    const [endDate, setEndDate] = useState("");
    const [title, setTitle] = useState("");
    const [content, setContent] = useState("");
    const [visibility, setVisibility] = useState("공개");
    const [openStart, setOpenStart] = useState(false);
    const [openEnd, setOpenEnd] = useState(false);

    const handleSubmit=()=>{
        const reviewData={
            isbn: isbn,
            rating,
            startDate,
            endDate,
            title,
            content,
            visibility,
        };
        alert("서평이 등록되었습니다.")
    }

    if(loading) return <div>책 정보를 불러오는 중 ... </div>
    if(!book) return <div>책 정보를 찾을 수 없습니다.</div>

    return(
        <div className="review-write-view">
            <div className="review-write-card">
                {/* 상단 책 정보 */}
            <div className="review-write-header">
                <img src={book.image} alt="" className="book-cover" />

                <div className="book-info-meta">
                    <div className="book-meta-wrapper">
                         <h2>{book.title}</h2>
                        <p className="author">{book.author}</p>
                    </div>
                    <div className="book-meta-wrapper">
                        <StarRate value={rating} onChange={setRating} />

                        {/* 날짜 선택 UI */}
                        <div className="date-row">
                            <div className="date-input" onClick={() => setOpenStart(true)}>
                                <CiCalendar className="calendar-icon" size={20} />
                                <span className={startDate ? "date-value" : "date-placeholder"}>
                                {startDate || "읽기 시작한 날"}
                                </span>
                            </div>
                            <span className="date-dash">—</span>
                            <div className="date-input" onClick={()=>setOpenEnd(true)}>
                                <CiCalendar className="calendar-icon" size={20}/>
                                <span className={endDate?"date-value":"date-placeholder"}>
                                    {endDate||"읽기 종료한 날짜"}
                                </span>
                            </div> 
                        </div>
                    </div>
                    
                </div>
                <button className="submit-btn" onClick={handleSubmit}>
                등록
                </button>
                
            </div>

            <div className="visibility-row">
                <div className="char-count">{content.length}/1000</div>
                <Dropdown
                value={visibility}
                onChange={setVisibility}
                options={["공개", "비공개"]}
                />
            </div>
            <div className="write-area-wrapper">
                <input
                className="title-input"
                placeholder="제목을 입력해주세요"
                value={title}
                onChange={(e) => setTitle(e.target.value)}
            />

            <textarea
                className="content-input"
                placeholder="내용을 입력해주세요"
                maxLength={1000}
                value={content}
                onChange={(e) => setContent(e.target.value)}
            />
            
            </div>
            

            {/* 날짜 모달 */}
            {openStart && ( //읽기 시작한 날짜
                <DatePickerModal
                onClose={() => setOpenStart(false)}
                onSelect={setStartDate}
                max={endDate||undefined} //종료일 보다 큰 날짜는 선택 불가
                />
            )}

            {openEnd && ( //읽기 종료한 날짜
                <DatePickerModal
                onClose={() => setOpenEnd(false)}
                onSelect={setEndDate}
                min={startDate||undefined} //시작일보다 작은 날짜는 선택 불가
                />
            )}
            </div>
            
        </div>
    )
};


export default ReviewWrite;