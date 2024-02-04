package book.store.mapper;

import book.store.config.MapperConfig;
import book.store.dto.book.BookDto;
import book.store.dto.book.BookDtoWithoutCategoryIds;
import book.store.dto.book.CreateBookRequestDto;
import book.store.model.Book;
import book.store.model.Category;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(config = MapperConfig.class)
public interface BookMapper {
    BookDto toDto(Book book);

    @AfterMapping
    default void setCategoryIds(Book book, @MappingTarget BookDto bookDto) {
        Set<Long> categoryIds = book.getCategories().stream()
                .map(Category::getId)
                .collect(Collectors.toSet());
        bookDto.setCategoryIds(categoryIds);
    }

    BookDtoWithoutCategoryIds toDtoWithoutCategories(Book book);

    Book toModel(CreateBookRequestDto requestDto);

    @AfterMapping
    default void setCategories(CreateBookRequestDto createRequestDto,
                               @MappingTarget Book book) {
        Set<Category> categoryIds = createRequestDto.getCategoryIds().stream()
                .map(Category::new)
                .collect(Collectors.toSet());
        book.setCategories(categoryIds);
    }

    Book updateBookFromDto(CreateBookRequestDto requestDto, @MappingTarget Book book);
}
