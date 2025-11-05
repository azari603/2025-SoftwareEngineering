import { createContext, useContext, useEffect, useState} from "react"

const BookStatusContext = createContext(null)

export function BookStatusProvider({children}){
    const [bookStatusMap, setBookStatusMap] = useState(() =>{
        const stored = localStorage.getItem("bookStatusMap")
        return stored?JSON.parse(stored):{}
    })

    //상태변경
    const updateStatus=(isbn, newStatus) =>{
        setBookStatusMap((prev)=>{
            const currentStatus=prev[isbn];
            let updated;
            if(currentStatus === newStatus){
                updated={...prev}
                delete updated[isbn]
            }else{
                updated={...prev,[isbn]:newStatus}
            }
            return updated
        })
    }

    //필터링
    const getBooksByStatus=(status)=>
        Object.entries(bookStatusMap).filter(([_,s])=>s===status).map(([isbn])=>isbn);

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

export const useBookStatus = () => useContext(BookStatusContext)