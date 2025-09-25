// src/components/BookList/BookList.jsx
import BookCard from "../BookCard/BookCard";
import CarouselLayout from "../ListLayout/CarouselLayout";
import HorizontalList from "../ListLayout/HorizontalList";

const BookList = ({ books, mode = "list", visibleCount = 3 }) => {
  if (mode === "carousel") {
    return (
      <CarouselLayout
        items={books}
        visibleCount={visibleCount}
        renderItem={(book, idx, ref) => (
          <BookCard key={idx} book={book} ref={ref} />
        )}
      />
    );
  }

  return (
    <HorizontalList
      items={books}
      renderItem={(book, idx) => <BookCard key={idx} book={book} />}
    />
  );
};

export default BookList;

