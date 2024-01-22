package book.store.service;

import book.store.dto.BookDto;
import book.store.dto.BookSearchParametersDto;
import book.store.dto.CreateBookRequestDto;
import java.util.List;
import org.springframework.data.domain.Pageable;

public interface BookService {
    List<BookDto> search(BookSearchParametersDto searchParameters);

    BookDto save(CreateBookRequestDto requestDto);

    BookDto findById(Long id);

    List<BookDto> findAll(Pageable pageable);

    BookDto updateBookById(Long id, CreateBookRequestDto requestDto);

    void deleteById(Long id);
}
