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
import { dummyBooks } from "./mocks/dummyBooks";
import { dummyReviews } from "./mocks/dummyReviews";
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
import BookSelectorView from "./pages/ReviewWrite/BookSelectorView"
import ReviewEditorView from "./pages/ReviewWrite/ReviewEditorView"
import RequireAuth from "./components/RequireAuth";
import ReviewWrite from "./pages/ReviewWrite/ReviewWrite";

export default function App() {
  const todaysBooks = dummyBooks;
  const todaysReviews = dummyReviews;
  const recommendedBooks = dummyBooks;
  const followingReviews = dummyReviews;


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
                <Home
                  todayBooks={todaysBooks}
                  todayReviews={todaysReviews}
                  recommendedBooks={recommendedBooks}
                  followingReviews={followingReviews}
                />}/>

                <Route path="/quiz/start" element={<QuizStart/>}/>
                <Route path="/quiz" element={<QuizPage/>}/>
                <Route path="/quiz/result" element={<QuizResult/>}/>
                <Route path="/search" element={<SearchPage/>}></Route>
                <Route path="/profile" element={<ProfilePage/>}/>
                <Route path="/profile/settings" element={<SettingsPage/>}/>
                <Route path="/profile/settings/edit" element={<ProfileEditPage/>}/>
                <Route path="/review/:id" element={<ReviewDetail/>}/>
                <Route path="/book/:isbn" element={<BookDetailPage/>}/>
                <Route path="/profile/library" element={<RequireAuth><MyLibrary/></RequireAuth>}/>
                
                <Route path="/write/book" element={<RequireAuth><BookSelectorView/></RequireAuth>}/>
                <Route path="/write/review" element={<RequireAuth><ReviewWrite/></RequireAuth>}/>
                
          </Route>
          
          <Route path="/login" element={<LoginPage />} />
          <Route path="/signup" element={<SignupLayout />}>
          <Route index element={<SignupPage />} />
          <Route path="success" element={<SignupSuccess />} />
          <Route path="email" element={<SignupEmail />} />
          </Route>
        </Routes>
        
      </Router>
      </LayoutProvider>
    </AuthProvider>
    
    
  );
}
