
export const dummyOverview = {
  totalRead: 120,
  avgRating: 4.1,
  ratingHistogram: { 
    0: 0, 1: 0, 2: 1, 3: 3, 4: 6, 5: 8 
  },
  timeline: [
    { month: "2025-01", count: 1 },
    { month: "2025-02", count: 6 },
    { month: "2025-03", count: 3 },
    { month: "2025-04", count: 5 },
    { month: "2025-05", count: 6 },
    { month: "2025-06", count: 3 },
    { month: "2025-07", count: 4 },
    { month: "2025-08", count: 6 },
  ],
  topAuthors: [
    { name: "베르나르 베르베르", count: 3 },
    { name: "윌리엄 셰익스피어", count: 3 },
    { name: "제인 오스틴", count: 3 },
    { name: "조지 오웰", count: 3 },
  ],
};

export const dummyStars = {
  0: 0,
  1: 0,
  2: 1,
  3: 3,
  4: 6,
  5: 8,
};

export const dummyTimeline = [
  { month: "2025-01", reviews: 1 },
  { month: "2025-02", reviews: 6 },
  { month: "2025-03", reviews: 3 },
  { month: "2025-04", reviews: 5 },
  { month: "2025-05", reviews: 6 },
  { month: "2025-06", reviews: 3 },
  { month: "2025-07", reviews: 4 },
  { month: "2025-08", reviews: 6 },
];

export const dummyAuthors = [
  {
    name: "베르나르 베르베르",
    count: 5,
    books: [
      {title:"개미", image:"/covers/cover1.png"},
      {title: "골렘의 예언 1",image:"/covers/cover2.png"},
      {title:"파피용",image:"/covers/cover3.png"},
      { title: "햄릿", image: "/covers/cover4.png" },
      { title: "오셀로", image: "/covers/cover5.png" },
      
    ]
  },
  {
    name: "윌리엄 셰익스피어",
    count: 1,
    books: [
      { title: "리어왕", image: "/covers/cover6.png" },
    ]
  },
  {
    name: "제인 오스틴",
    count: 8,
    books: [
      { title: "오만과 편견", image: "/covers/cover7.png" },
      { title: "엠마", image: "/covers/cover8.png" },
      { title: "설득", image: "/covers/cover9.png" },
      { title: "1984", image: "/covers/cover10.png" },
      { title: "동물농장", image: "/covers/cover1.png" },
      { title: "버마 시절", image: "/covers/cover2.png" },
    ]
  },
  {
    name: "조지 오웰",
    count: 3,
    books: [
      { title: "1984", image: "/covers/cover10.png" },
      { title: "동물농장", image: "/covers/cover1.png" },
      { title: "버마 시절", image: "/covers/cover2.png" },
    ]
  },
];

export const dummyGoals = {
  goal: 20,
  achieved: 12,
  rate: 60,
};
