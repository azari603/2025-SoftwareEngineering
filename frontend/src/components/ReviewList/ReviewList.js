import ReviewCard from "../ReviewCard/ReviewCard";
import CarouselLayout from "../ListLayout/CarouselList";
import HorizontalList from "../ListLayout/HorizontalList";

const ReviewList = ({ reviews, mode = "list", visibleCount = 3, variant = "withBook" }) => {
  if (mode === "carousel") {
    return (
      <CarouselLayout
        items={reviews}
        visibleCount={visibleCount}
        renderItem={(review, idx, ref) => (
          <ReviewCard key={idx} review={review} ref={ref} variant={variant} />
        )}
      />
    );
  }
};

export default ReviewList;
