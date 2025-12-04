import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import LoginPage from "./pages/LoginPage/LoginPage";
import SignupPage from "./pages/SignupPage/SignupPage";
import SignupLayout from "./pages/SignupPage/SignupLayout";
import SignupSuccess from "./pages/SignupPage/SignupSuccess/SignupSuccess";
import SignupEmail from "./pages/SignupPage/SignupEmail/SignupEmail";
import QuizStart from "./pages/Quiz/QuizStart/QuizStart";
import QuizPage from "./pages/Quiz/QuizPage/QuizPage";
import Home from "./pages/Home/Home";
import QuizResult from "./pages/Quiz/QuizResult/QuizResult";
import { AuthProvider } from "./context/AuthContext";
import BaseLayout from "./components/Layout/BaseLayout";
import SearchPage from "./pages/SearchPage/SearchPage"
import ProfilePage from "./pages/ProfilePage/ProfilePage";
import ProfileEditPage from "./pages/ProfilePage/ProfileEditPage/ProfileEditPage";
import SettingsPage from "./pages/ProfilePage/SettingsPage/SettingsPage";
import ReviewDetail from "./pages/DetailedReview/DetailedReview";
import BookDetailPage from "./pages/BookDetail/BookDetailPage";
import { LayoutProvider } from "./context/LayoutContext";
import MyLibrary from "./pages/MyLibraryPage/MyLibrary";
import ScrollToTop from "./components/ScrollToTop";
import MyReview from "./pages/MyReview/MyReviewPage";
import SidebarLayout from "./components/Layout/SidebarLayout";
import BookSelectorView from "./pages/ReviewWrite/BookSelectorView"
import RequireAuth from "./components/RequireAuth";
import ReviewWrite from "./pages/ReviewWrite/ReviewWrite";
import Stats from "./pages/Stats/StatsPage";
import FeedPage from "./pages/FeedPage/FeedPage";
import FindIdPage from "./pages/FindIdPage/FindIdPage";
import FindIdSuccessPage from "./pages/FindIdPage/FindIdSuccessPage";
import FindPasswordPage from "./pages/FindPasswordPage/FindPasswordPage";
import FindPasswordSuccessPage from "./pages/FindPasswordPage/FindPasswordSuccessPage";

export default function App() {
  return (
    <AuthProvider>
    <LayoutProvider>
      <Router>
        <ScrollToTop />
        <Routes>
          <Route element={<BaseLayout/>}>
          <Route
            path="/"
            element={
                <Home/>}/>

                <Route path="/quiz/start" element={<QuizStart/>}/>
                <Route path="/quiz" element={<QuizPage/>}/>
                <Route path="/quiz/result" element={<QuizResult/>}/>
                <Route path="/search" element={<SearchPage/>}></Route>
                <Route path="/profile" element={<ProfilePage/>}/>
                <Route path="/profile/:username" element={<ProfilePage/>}/>
                <Route path="/profile/settings" element={<SettingsPage/>}/>
                <Route path="/profile/settings/edit" element={<ProfileEditPage/>}/>
                <Route path="/review/:reviewId" element={<ReviewDetail/>}/>
                <Route path="/review/edit/:reviewId" element={<ReviewWrite />} />
                <Route path="/book/:bookId" element={<BookDetailPage/>}/>
                
                <Route path="/write/book" element={<RequireAuth><BookSelectorView/></RequireAuth>}/>
                <Route path="/write/review" element={<RequireAuth><ReviewWrite/></RequireAuth>}/>
                <Route path="/feed" element={<FeedPage/>}/>
          </Route>

          <Route element={<SidebarLayout/>}>
            <Route path="/profile/library" element={<RequireAuth><MyLibrary/></RequireAuth>}/>
            <Route path="/profile/reviews" element={<RequireAuth><MyReview/></RequireAuth>}/>    
            <Route path="/profile/stats" element={<RequireAuth><Stats/></RequireAuth>}/>    
            <Route path="/profile/reviews" element={<RequireAuth><MyReview/></RequireAuth>}/>                
               
                
          </Route>
          <Route path="/signup/email" element={<SignupEmail />} />
          <Route path="/find-id" element={<FindIdPage/>}/>
          <Route path="/find-id/success" element={<FindIdSuccessPage/>}/>
          <Route path="/find-password" element={<FindPasswordPage />} />
          <Route path="/find-password/success" element={<FindPasswordSuccessPage />} />
          <Route path="/login" element={<LoginPage />} />
          <Route path="/signup" element={<SignupLayout />}>
          <Route index element={<SignupPage />} />
          <Route path="success" element={<SignupSuccess />} />

          </Route>
        </Routes>
      </Router>
      </LayoutProvider>
    </AuthProvider>
    
    
  );
}
