/*import { createContext, useContext, useEffect, useState} from "react"
import { updateBookStatus, removeBookStatus, getBooksByStatus } from "../api/bookAPI"

const BookStatusContext = createContext(null)

export function BookStatusProvider({children}){
    //책의 상태를 저장하는 객체
    const [bookStatusMap, setBookStatusMap] = useState({})

    //특정 책의 상태를 변경
    const updateStatus=async(isbn, newStatus) =>{
        const res=await updateBookStatus(isbn, newStatus);
        if(res.success){
            setBookStatusMap((prev)=>({...prev, [isbn]:newStatus}));
        }
    }

    const removeStatusByISBN=async(isbn)=>{
        const res = await removeBookStatus(isbn);
        if(res.success)
    }

    //특정 상태에 해당하는 책들의 isbn 목록 반환
    const getBooksByStatus=(status)=>
        Object.entries(bookStatusMap).filter(([_,s])=>s===status).map(([isbn])=>isbn);

    //상태가 바뀔때마다 localStorage에 저장
    useEffect(()=>{
        localStorage.setItem("bookStatusMap",JSON.stringify(bookStatusMap))
    },[bookStatusMap])

    return(
        <BookStatusContext.Provider
            value={{bookStatusMap, updateStatus, getBooksByStatus}}>
                {children}
            </BookStatusContext.Provider>
    )
}

export const useBookStatus = () => useContext(BookStatusContext)*/