INSERT INTO books (id, title, author, isbn, price, description, cover_image, is_deleted)
VALUES
  (1, 'Book 1', 'Author 1', 'ISBN-1', 10.50, 'Description for Book 1', 'cover1.jpg', false),
  (2, 'Book 2', 'Author 2', 'ISBN-2', 15.75, 'Description for Book 2', 'cover2.jpg', false),
  (3, 'Book 3', 'Author 3', 'ISBN-3', 20.00, 'Description for Book 3', 'cover3.jpg', false);

INSERT INTO books_categories (book_id, category_id)
VALUES
  (1, 1),
  (2, 2),
  (3, 3);
