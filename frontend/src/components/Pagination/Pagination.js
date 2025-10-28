import "./Pagination.css"
function Pagination({ currentPage, totalCount, pageSize, onPageChange }) {
  // 전체 페이지 수 계산
  const totalPages = Math.ceil(totalCount / pageSize);

  // 전체 페이지가 1 이하라면(=한 페이지면) 아예 페이지네이션을 안 보여줘도 됨
  if (totalPages <= 1) return null;

  // 페이지 번호 배열을 만든다. 예: totalPages = 5 => [1,2,3,4,5]
  const pageNumbers = Array.from({ length: totalPages }, (_, idx) => idx + 1);

  // 이전 페이지로 이동
  const handlePrev = () => {
    if (currentPage > 1) {
      onPageChange(currentPage - 1);
    }
  };

  // 다음 페이지로 이동
  const handleNext = () => {
    if (currentPage < totalPages) {
      onPageChange(currentPage + 1);
    }
  };

  return (
    <nav className="pagination">
      {/* 이전 버튼 */}
      <button
        className="page-btn prev-btn"
        onClick={handlePrev}
        disabled={currentPage === 1}
        aria-label="이전 페이지"
      >
        &lt;
      </button>

      {/* 개별 페이지 번호 버튼들 */}
      {pageNumbers.map((num) => (
        <button
          key={num}
          className={
            "page-btn page-number-btn" +
            (num === currentPage ? " active" : "")
          }
          onClick={() => onPageChange(num)}
          aria-current={num === currentPage ? "page" : undefined}
        >
          {num}
        </button>
      ))}

      {/* 다음 버튼 */}
      <button
        className="page-btn next-btn"
        onClick={handleNext}
        disabled={currentPage === totalPages}
        aria-label="다음 페이지"
      >
        &gt;
      </button>
    </nav>
  );
}

export default Pagination;