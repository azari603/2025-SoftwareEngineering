import BookCard from "../BookCard/BookCard";
import CarouselLayout from "../ListLayout/CarouselList";
import HorizontalList from "../ListLayout/HorizontalList";

const BookList = ({ books, mode = "list", visibleCount = 3, cardSize="md"}) => {
  if (mode === "carousel") {
    return (
      <CarouselLayout
        items={books}
        visibleCount={visibleCount}
        renderItem={(book, idx, ref) => (
          <BookCard key={idx} book={book} ref={ref} size={cardSize} />
        )}
      />
    );
  }
  return (
    <HorizontalList
      items={books}
      renderItem={(book, idx) => <BookCard key={idx} book={book} size={cardSize}/>}
    />
  );
};

export default BookList;

