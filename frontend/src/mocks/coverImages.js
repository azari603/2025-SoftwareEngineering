const base = process.env.PUBLIC_URL || "";

const withBase = (p) =>
  `${String(base).replace(/\/+$/, "")}/${String(p).replace(/^\/+/, "")}`;

export const coverImages = Array.from(
  { length: 10 },
  (_, i) => withBase(`covers/cover${i + 1}.png`)
);