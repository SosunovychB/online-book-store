package book.store.service;

import book.store.dto.BookDto;
import book.store.dto.CreateBookRequestDto;
import java.util.List;

public interface BookService {
    BookDto save(CreateBookRequestDto requestDto);

    BookDto findById(Long id);

    List<BookDto> findAll();

    BookDto updateBookById(Long id, CreateBookRequestDto requestDto);

    void deleteById(Long id);
}
