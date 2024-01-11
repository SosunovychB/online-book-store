package book.store.service;

import book.store.dto.BookDto;
import book.store.dto.CreateBookRequestDto;
import java.util.List;
import java.util.Optional;

public interface BookService {
    BookDto save(CreateBookRequestDto requestDto);

    Optional<BookDto> findById(Long id);

    List<BookDto> findAll();
}
