import axios from "axios";

const axiosInstance=axios.create({
    baseURL: process.env.REACT_APP_API_BASE_URL,
    withCredentials: true,
});

//요청 인터셉터
axiosInstance.interceptors.request.use((config)=>{
    const token=localStorage.getItem("accessToken");
    
    if(token){
        config.headers.Authorization=`Bearer ${token}`
    }
    
    return config;
})

//응답 인터셉터
axiosInstance.interceptors.response.use(
    (res)=>res,
    async (error)=>{
        const originalRequest=error.config;

        if(error.response?.status===401&&!originalRequest._retry){
            originalRequest._retry=true;
            try{
                const {data}=await axiosInstance.post("/auth/token/refresh");
                localStorage.setItem("accessToken",data.accessToken);
                originalRequest.headers.Authorization=`Bearer ${data.accessToken}`;
                return axiosInstance(originalRequest);
            }catch(err){
                localStorage.removeItem("accessToken");
                window.location.href="/login"; 
            }
        }
        /*if(error.response?.status===403){
            localStorage.removeItem("accessToken");
            localStorage.removeItem("refreshToken");
            window.location.href="/login";
        }*/
        if (error.response?.status === 500) {
        alert("서버 오류가 발생했습니다. 잠시 후 다시 시도해주세요.");
        }
        return Promise.reject(error);
    }
)

export default axiosInstance;