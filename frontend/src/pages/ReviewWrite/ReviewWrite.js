import { useEffect, useState } from "react";
import { useLocation, useNavigate, useParams } from "react-router-dom";
import StarRate from "../../components/StarRate/StarRate";
import DatePickerModal from "../../components/Modal/DatePickerModal/DatePickerModal";
import Dropdown from "../../components/Dropdown/Dropdown";
import { fetchBookDetail, getBookByISBN, updateReview } from "../../api/bookAPI";
import { CiCalendar } from "react-icons/ci";
import { createReview, fetchReviewDetail } from "../../api/reviewAPI";
import "./ReviewWrite.css";

const ReviewWrite = () => {
    const location = useLocation();
    const params=new URLSearchParams(location.search);
    const navigate=useNavigate();
    const id=params.get("id");
    const {reviewId}=useParams();
    const isEdit=!!reviewId;
    const[book, setBooks]=useState(null);
    const[loading, setLoading]=useState(true);

    const [rating, setRating] = useState(0);
    const [startDate, setStartDate] = useState("");
    const [endDate, setEndDate] = useState("");
    const [title, setTitle] = useState("");
    const [content, setContent] = useState("");
    const [visibility, setVisibility] = useState("공개");
    const [openStart, setOpenStart] = useState(false);
    const [openEnd, setOpenEnd] = useState(false);

    const visibilityMap={
        "공개":"PUBLIC",
        "비공개":"PRIVATE",
    }
    const reverseVisibilityMap = {
        "PUBLIC": "공개",
        "PRIVATE": "비공개",
        };

    useEffect(()=>{
        if(isEdit&&reviewId){
            fetchReviewDetail(reviewId).then(data=>{
                setTitle(data.title);
                setContent(data.text);
                setRating(data.starRting);
                setEndDate(data.book.finishDate);
                setStartDate(data.book.startDate);

                setVisibility(reverseVisibilityMap[data.visibility]);
                setBooks(data.book);
                setLoading(false)


            })
        }
    },[isEdit]);
    //책 정보 불러오기
    useEffect(()=>{
        if(!isEdit&&id){
            async function fetchBooks(){
            const res=await fetchBookDetail(id);
            if(!res.ok){
                alert(res.message);
                return;
            }
            setBooks(res);
            setLoading(false);
        }
        fetchBooks()
        }
        
    },[id]);
    
    

    

    const handleSubmit= async ()=>{
        const payload={
        bookId: book.id,
        title,
        text: content,
        starRating: rating,
        startDate,
        finishDate: endDate,
        visibility: visibilityMap[visibility],
        status:"PUBLISHED",
       }

       if(isEdit)
        {   await updateReview(reviewId,payload)
            alert("서평이 수정되었습니다.");
            navigate(`/review/${reviewId}`);
            return;
        }
       else{
        let result=await createReview(payload);
        if(!result.success){
        if(result.code==="BOOK_NOT_FOUND") alert("책 정보를 찾을 수 없습니다.");
        else if(result.code==="VALIDATION_ERROR") alert("입력값이 유효하지 않습니다.");
        else alert("서평 등록 중 오류가 발생했습니다.");
        return;
       }
        alert("서평이 등록되었습니다.");
        navigate(`/review/${result.reviewId}`);
        return;
       }
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
                         <h2>{book.name}</h2>
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