package book.store.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import book.store.dto.book.BookDto;
import book.store.dto.book.BookDtoWithoutCategoryIds;
import book.store.dto.book.CreateBookRequestDto;
import book.store.exception.EntityNotFoundException;
import book.store.mapper.BookMapper;
import book.store.model.Book;
import book.store.model.Category;
import book.store.repository.book.BookRepository;
import book.store.service.impl.BookServiceImpl;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
public class BookServiceImplTest {
    @Mock
    private BookRepository bookRepository;
    @Mock
    private BookMapper bookMapper;
    @InjectMocks
    private BookServiceImpl bookServiceImpl;

    @Test
    @DisplayName("Verify save() method works")
    public void save_ValidBook_ReturnsValidBookDto() {
        //given
        CreateBookRequestDto createBookRequestDto = new CreateBookRequestDto()
                .setTitle("Kobzar")
                .setAuthor("Taras Shevchenko")
                .setIsbn("TS-01")
                .setPrice(new BigDecimal("99.99"))
                .setCategoryIds(Collections.singleton(1L));
        Long bookId = 1L;
        Book book = new Book()
                .setId(bookId)
                .setTitle(createBookRequestDto.getTitle())
                .setAuthor(createBookRequestDto.getAuthor())
                .setIsbn(createBookRequestDto.getIsbn())
                .setPrice(createBookRequestDto.getPrice())
                .setCategories(createBookRequestDto.getCategoryIds().stream()
                        .map(Category::new)
                        .collect(Collectors.toSet()))
                .setDeleted(false);
        BookDto savedBookDto = new BookDto()
                .setId(book.getId())
                .setTitle(book.getTitle())
                .setAuthor(book.getAuthor())
                .setIsbn(book.getIsbn())
                .setPrice(book.getPrice())
                .setCategoryIds(book.getCategories().stream()
                        .map(Category::getId)
                        .collect(Collectors.toSet()));

        Mockito.when(bookMapper.toModel(createBookRequestDto)).thenReturn(book);
        Mockito.when(bookRepository.save(book)).thenReturn(book);
        Mockito.when(bookMapper.toDto(book)).thenReturn(savedBookDto);

        //when
        BookDto actualSavedBookDto = bookServiceImpl.save(createBookRequestDto);

        //then
        assertEquals(savedBookDto, actualSavedBookDto);

        verify(bookMapper, Mockito.times(1)).toModel(createBookRequestDto);
        verify(bookRepository, Mockito.times(1)).save(book);
        verify(bookMapper, Mockito.times(1)).toDto(book);
        verifyNoMoreInteractions(bookMapper);
        verifyNoMoreInteractions(bookRepository);
    }

    @Test
    @DisplayName("Verify findById() method works for valid bookId")
    public void findById_ValidBookId_ReturnsValidBookDto() {
        //given
        Long bookId = 1L;
        Book book = createBook(bookId);
        BookDto bookDto = createBookDto(book);

        Mockito.when(bookRepository.findBookById(bookId)).thenReturn(Optional.of(book));
        Mockito.when(bookMapper.toDto(book)).thenReturn(bookDto);

        //when
        BookDto actualBookDto = bookServiceImpl.findById(bookId);

        //then
        assertEquals(bookDto, actualBookDto);

        verify(bookRepository, Mockito.times(1)).findBookById(bookId);
        verify(bookMapper, Mockito.times(1)).toDto(book);
        verifyNoMoreInteractions(bookRepository);
        verifyNoMoreInteractions(bookMapper);
    }

    @Test
    @DisplayName("Verify findById() method throws the EntityNotFoundException "
            + "for invalid bookId")
    public void findById_InvalidBookId_ThrowsException() {
        //given
        Long bookId = -1L;

        Mockito.when(bookRepository.findBookById(bookId)).thenThrow(
                new EntityNotFoundException("Can't find book with id: " + bookId));

        //when
        Exception exception = assertThrows(
                EntityNotFoundException.class,
                () -> bookServiceImpl.findById(bookId));

        //then
        String expectedMessage = "Can't find book with id: " + bookId;
        String actualMessage = exception.getMessage();
        assertEquals(expectedMessage, actualMessage);

        verify(bookRepository, Mockito.times(1)).findBookById(bookId);
        verifyNoMoreInteractions(bookRepository);
    }

    @Test
    @DisplayName("Verify findAll() method works")
    public void findAll_ValidPageable_ReturnsAllBooks() {
        // given
        Long bookId = 1L;
        Book book = createBook(bookId);
        BookDto bookDto = createBookDto(book);
        Pageable pageable = PageRequest.of(0,10);
        List<Book> books = List.of(book);
        Page<Book> bookPage = new PageImpl<>(books, pageable, books.size());

        Mockito.when(bookRepository.findAllBooks(pageable)).thenReturn(bookPage);
        Mockito.when(bookMapper.toDto(book)).thenReturn(bookDto);

        //when
        List<BookDto> actualBookDtos = bookServiceImpl.findAll(pageable);

        //then
        assertThat(actualBookDtos).hasSize(books.size());
        assertEquals(bookDto, actualBookDtos.get(0));

        Mockito.verify(bookRepository, Mockito.times(1)).findAllBooks(pageable);
        Mockito.verify(bookMapper, Mockito.times(books.size())).toDto(any());
        verifyNoMoreInteractions(bookRepository);
        verifyNoMoreInteractions(bookMapper);
    }

    @Test
    @DisplayName("Verify findAllByCategoryId() method works")
    public void findAllByCategoryId_ValidCategoryId_ReturnsAllBooks() {
        // given
        Long categoryId = 1L;
        Long bookId = 1L;
        Book book = createBook(bookId);
        BookDtoWithoutCategoryIds bookDtoWithoutCategoryId = createBookDtoWithoutCategoryIds(book);

        Mockito.when(bookRepository.findBooksByCategoryId(categoryId)).thenReturn(List.of(book));
        Mockito.when(bookMapper.toDtoWithoutCategories(book)).thenReturn(bookDtoWithoutCategoryId);

        //when
        List<BookDtoWithoutCategoryIds> actualBooksWithoutCategoryId =
                bookServiceImpl.findAllBooksByCategoryId(categoryId);

        //then
        assertThat(actualBooksWithoutCategoryId).hasSize(1);
        assertEquals(bookDtoWithoutCategoryId, actualBooksWithoutCategoryId.get(0));

        verify(bookRepository, Mockito.times(1)).findBooksByCategoryId(categoryId);
        verify(bookMapper, Mockito.times(1)).toDtoWithoutCategories(book);
        verifyNoMoreInteractions(bookRepository);
        verifyNoMoreInteractions(bookMapper);
    }

    @Test
    @DisplayName("Verify updateBookById() method works for valid bookId")
    public void updateBookById_ValidBookId_ReturnsUpdatedBookDto() {
        //given
        Long bookId = 1L;
        Book book = createBook(bookId);
        CreateBookRequestDto createBookRequestDto = new CreateBookRequestDto()
                .setTitle("title")
                .setAuthor("author")
                .setIsbn("isbn")
                .setPrice(new BigDecimal("10.10"))
                .setCategoryIds(Collections.singleton(1L));
        Book updatedBook = new Book()
                .setId(book.getId())
                .setTitle(createBookRequestDto.getTitle())
                .setAuthor(createBookRequestDto.getAuthor())
                .setIsbn(createBookRequestDto.getIsbn())
                .setPrice(createBookRequestDto.getPrice())
                .setCategories(createBookRequestDto.getCategoryIds().stream()
                        .map(Category::new)
                        .collect(Collectors.toSet()))
                .setDeleted(book.isDeleted());
        BookDto updatedBookDto = new BookDto()
                .setId(updatedBook.getId())
                .setTitle(updatedBook.getTitle())
                .setAuthor(updatedBook.getAuthor())
                .setIsbn(updatedBook.getIsbn())
                .setPrice(updatedBook.getPrice())
                .setCategoryIds(updatedBook.getCategories().stream()
                        .map(Category::getId)
                        .collect(Collectors.toSet()));

        Mockito.when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        Mockito.when(bookMapper.updateBookFromDto(createBookRequestDto, book))
                .thenReturn(updatedBook);
        Mockito.when(bookRepository.save(updatedBook)).thenReturn(updatedBook);
        Mockito.when(bookMapper.toDto(updatedBook)).thenReturn(updatedBookDto);

        //when
        BookDto actualUpdatedBookDto = bookServiceImpl.updateBookById(bookId, createBookRequestDto);

        //then
        assertEquals(updatedBookDto, actualUpdatedBookDto);

        verify(bookRepository, Mockito.times(1)).findById(bookId);
        verify(bookMapper, Mockito.times(1)).updateBookFromDto(
                createBookRequestDto, book);
        verify(bookRepository, Mockito.times(1)).save(updatedBook);
        verify(bookMapper, Mockito.times(1)).toDto(updatedBook);
        verifyNoMoreInteractions(bookRepository);
        verifyNoMoreInteractions(bookMapper);
    }

    @Test
    @DisplayName("Verify updateBookById() method throws the EntityNotFoundException "
            + "for invalid bookId")
    public void updateBookById_NotValidBookId_ReturnsUpdatedBookDto() {
        //given
        Long bookId = -1L;
        CreateBookRequestDto createBookRequestDto = new CreateBookRequestDto()
                .setTitle("title")
                .setAuthor("author")
                .setIsbn("isbn")
                .setPrice(new BigDecimal("10.10"))
                .setCategoryIds(Collections.singleton(1L));

        Mockito.when(bookRepository.findById(bookId)).thenThrow(
                new EntityNotFoundException("Can't find book with id: " + bookId));

        //when
        Exception exception = assertThrows(
                EntityNotFoundException.class,
                () -> bookServiceImpl.updateBookById(bookId, createBookRequestDto));

        //then
        String expectedMessage = "Can't find book with id: " + bookId;
        String actualMessage = exception.getMessage();
        assertEquals(expectedMessage, actualMessage);

        verify(bookRepository, Mockito.times(1)).findById(bookId);
        verifyNoMoreInteractions(bookRepository);
    }

    @Test
    @DisplayName("Verify deleteById() works")
    public void deleteById_ValidBookId_WorksOnlyOnce() {
        // given
        Long bookId = 1L;

        // when
        bookServiceImpl.deleteById(bookId);

        //then
        verify(bookRepository, Mockito.times(1)).deleteById(anyLong());
        verifyNoMoreInteractions(bookRepository);
    }

    private Book createBook(Long bookId) {
        Category category = new Category().setId(1L);
        return new Book()
                .setId(bookId)
                .setTitle("Kobzar")
                .setAuthor("Taras Shevchenko")
                .setIsbn("TS-01")
                .setPrice(new BigDecimal("99.99"))
                .setCategories(Collections.singleton(category))
                .setDeleted(false);
    }

    private BookDto createBookDto(@NotNull Book book) {
        return new BookDto()
                .setId(book.getId())
                .setTitle(book.getTitle())
                .setAuthor(book.getAuthor())
                .setIsbn(book.getIsbn())
                .setPrice(book.getPrice())
                .setDescription(book.getDescription())
                .setCoverImage(book.getCoverImage())
                .setCategoryIds(book.getCategories().stream()
                        .map(Category::getId)
                        .collect(Collectors.toSet()));
    }

    private BookDtoWithoutCategoryIds createBookDtoWithoutCategoryIds(@NotNull Book book) {
        return new BookDtoWithoutCategoryIds()
                .setId(book.getId())
                .setTitle(book.getTitle())
                .setAuthor(book.getAuthor())
                .setIsbn(book.getIsbn())
                .setPrice(book.getPrice())
                .setDescription(book.getDescription())
                .setCoverImage(book.getCoverImage());
    }
}
