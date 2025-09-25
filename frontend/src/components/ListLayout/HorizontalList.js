import "./HorizontalList.css";

const HorizontalList = ({ items, renderItem }) => {
  return (
    <div className="horizontal-list">
      {items.map((item, idx) => renderItem(item, idx))}
    </div>
  );
};

export default HorizontalList;
