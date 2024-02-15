package book.store.repository.book;

import book.store.model.Book;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

public interface BookRepository extends JpaRepository<Book, Long>, JpaSpecificationExecutor<Book> {
    @Query("FROM Book b LEFT JOIN FETCH b.categories с WHERE b.id = :id")
    Optional<Book> findBookById(Long id);

    @Query("FROM Book b LEFT JOIN FETCH b.categories")
    Page<Book> findAllBooks(Pageable pageable);

    @Query("FROM Book b LEFT JOIN FETCH b.categories c WHERE c.id = :categoryId")
    List<Book> findBooksByCategoryId(Long categoryId);
}
