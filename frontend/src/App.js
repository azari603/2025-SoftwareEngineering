import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import LoginPage from "./pages/LoginPage/LoginPage";
import SignupPage from "./pages/SignupPage/SignupPage";
import SignupLayout from "./pages/SignupPage/SignupLayout";
import SignupSuccess from "./pages/SignupPage/SignupSuccess/SignupSuccess";
import SignupEmail from "./pages/SignupPage/SignupEmail/SignupEmail";
import Home from "./pages/Home/Home";
import { dummyBooks } from "./mocks/dummyBooks";
import { dummyReviews } from "./mocks/dummyReviews";

export default function App() {
  const isLoggedIn = false;
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
      </Routes>
    </Router>
    
  );
}
