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
    }else{
         delete config.headers.Authorization;
    }
    
    return config;
})

//응답 인터셉터
axiosInstance.interceptors.response.use(
    (res)=>res,
    async (error)=>{
        const originalRequest=error.config;
        if (originalRequest.url.includes("/auth/token/refresh")) {
            return Promise.reject(error);
    }

        if(error.response?.status===401&&!originalRequest._retry){
            originalRequest._retry=true;
            try{
                const {data}=await axiosInstance.post("/auth/token/refresh");
                localStorage.setItem("accessToken",data.data.accessToken);
                originalRequest.headers.Authorization=`Bearer ${data.accessToken}`;
                return axiosInstance(originalRequest);
            }catch(err){
                localStorage.removeItem("accessToken");
            }
        }
        /*if(error.response?.status===403){
            localStorage.removeItem("accessToken");
            localStorage.removeItem("refreshToken");
            window.location.href="/login";
        }*/
        
        return Promise.reject(error);
    }
)

export default axiosInstance;