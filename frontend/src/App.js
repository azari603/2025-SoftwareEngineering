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



export default function App() {
  const isLoggedIn = true;
  const nickname = "빙봉";

  const todaysBooks = dummyBooks;
  const todaysReviews = dummyReviews;
  const recommendedBooks = dummyBooks;
  const followingReviews = dummyReviews;


  return (
    <Router>
      <Routes>
        <Route
          path="/"
          element={
            <Home
              isLoggedIn={isLoggedIn}
              nickname={nickname}
              todayBooks={todaysBooks}
              todayReviews={todaysReviews}
              recommendedBooks={recommendedBooks}
              followingReviews={followingReviews}
            />
          }
        />
        <Route path="/login" element={<LoginPage />} />

        <Route path="/signup" element={<SignupLayout />}>
          <Route index element={<SignupPage />} />
          <Route path="success" element={<SignupSuccess />} />
          <Route path="email" element={<SignupEmail />} />
        </Route>

        <Route path="/quiz/start" element ={<QuizStart/>}/>
        <Route path="/quiz" element={<QuizPage/>}/>
        <Route path="/quiz/result" element={<QuizResult/>} />
      </Routes>
    </Router>
    
  );
}
