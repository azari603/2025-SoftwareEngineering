import logo from './logo.svg';
import './App.css';
import BookCard from "../src/components/BookCard/BookCard"
import BookList from "../src/components/BookList/BookList"
import sampleImg from "./assets/sample.png"

//sample data
const books = [
  { title: "책제목1", author: "저자1", image: sampleImg },
  { title: "책제목2", author: "저자2", image: sampleImg },
  { title: "책제목3", author: "저자3", image: sampleImg },
  { title: "책제목4", author: "저자4", image: sampleImg },
  { title: "책제목5", author: "저자5", image: sampleImg },
  { title: "책제목6", author: "저자1", image: sampleImg },
  { title: "책제목7", author: "저자2", image: sampleImg },
  { title: "책제목8", author: "저자3", image: sampleImg },
];

function App() {
  return (
    <div className="App">
      <BookList books={books} mode='list' visibleCount={4}/>
    </div>
  );
}

export default App;
