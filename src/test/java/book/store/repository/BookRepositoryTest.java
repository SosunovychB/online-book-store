package book.store.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;

import book.store.model.Book;
import book.store.model.Category;
import book.store.repository.book.BookRepository;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.jdbc.Sql;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql(scripts = {"classpath:database/categories/add-categories-to-categories-table.sql",
        "classpath:database/books/add-books-to-books-table.sql"},
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
@Sql(scripts = {"classpath:database/categories/delete-categories-from-categories-table.sql",
        "classpath:database/books/delete-books-from-books-table.sql"},
        executionPhase = Sql.ExecutionPhase.AFTER_TEST_CLASS)
public class BookRepositoryTest {
    @Autowired
    private BookRepository bookRepository;

    @Test
    @DisplayName("Verify findBookById() method works")
    public void findBookById_ValidBookId_ReturnsOptionalOfBook() {
        //given
        Book expectedBook = createModelBook2();
        Optional<Book> expectedBookOptional = Optional.of(expectedBook);
        Long bookId = expectedBook.getId();

        //when
        Optional<Book> actualBookOptional = bookRepository.findBookById(bookId);

        //then
        assertEquals(expectedBookOptional.orElseThrow().getIsbn(),
                actualBookOptional.orElseThrow().getIsbn());
        assertEquals(expectedBookOptional.orElseThrow().getCategories(),
                actualBookOptional.orElseThrow().getCategories());
    }

    @Test
    @DisplayName("Verify findAllBooks() method works")
    public void findAllBooks_ValidPageable_ReturnsBookPage() {
        //given
        Book expectedBook = createModelBook2();
        Pageable pageable1 = PageRequest.of(0, 10);
        Pageable pageable2 = PageRequest.of(1, 1);

        //when
        Page<Book> actualAllBooks1 = bookRepository.findAllBooks(pageable1);
        Page<Book> actualAllBooks2 = bookRepository.findAllBooks(pageable2);

        //then
        assertEquals(3, actualAllBooks1.stream().toList().size());
        assertEquals(1, actualAllBooks2.stream().toList().size());
        assertEquals(expectedBook, actualAllBooks1.stream().toList().get(1));
        assertEquals(expectedBook, actualAllBooks2.stream().toList().get(0));
    }

    @Test
    @DisplayName("Verify findBooksByCategoryId() method works")
    public void findBooksByCategoryId_ValidAndInvalidCategoryId_ReturnsListOfBooks() {
        //when
        List<Book> booksByCategoryId1 = bookRepository.findBooksByCategoryId(1L);
        List<Book> booksByCategoryId3 = bookRepository.findBooksByCategoryId(3L);
        List<Book> booksByCategoryId4 = bookRepository.findBooksByCategoryId(4L);

        //then
        assertEquals(1, booksByCategoryId1.size());
        assertEquals(1, booksByCategoryId3.size());
        assertEquals(0, booksByCategoryId4.size());
    }

    private Category createModelCategory2() {
        return new Category()
                .setId(2L)
                .setName("Category 2")
                .setDescription("Description for Category 2")
                .setDeleted(false);
    }

    private Book createModelBook2() {
        Set<Category> categorySet = new HashSet<>();
        categorySet.add(createModelCategory2());
        return new Book()
                .setId(2L)
                .setTitle("Book 2")
                .setAuthor("Author 2")
                .setIsbn("ISBN-2")
                .setPrice(new BigDecimal("15.75"))
                .setCategories(categorySet)
                .setDescription("Description for Book 2")
                .setCoverImage("cover2.jpg")
                .setDeleted(false);
    }
}
