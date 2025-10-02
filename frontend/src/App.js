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

export default function App() {
  const todaysBooks = dummyBooks;
  const todaysReviews = dummyReviews;
  const recommendedBooks = dummyBooks;
  const followingReviews = dummyReviews;


  return (
    <AuthProvider>
      <Router>
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
          </Route>
          
          <Route path="/login" element={<LoginPage />} />
          <Route path="/signup" element={<SignupLayout />}>
          <Route index element={<SignupPage />} />
          <Route path="success" element={<SignupSuccess />} />
          <Route path="email" element={<SignupEmail />} />
          </Route>
        </Routes>
      </Router>
    </AuthProvider>
    
    
  );
}
