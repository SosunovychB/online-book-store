package book.store.mapper;

import book.store.config.MapperConfig;
import book.store.dto.category.CategoryDto;
import book.store.dto.category.CreateCategoryRequestDto;
import book.store.model.Category;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(config = MapperConfig.class)
public interface CategoryMapper {
    CategoryDto toDto(Category category);

    Category toEntity(CreateCategoryRequestDto createCategoryRequestDto);

    Category updateCategoryFromRequestDto(CreateCategoryRequestDto createCategoryRequestDto,
                                       @MappingTarget Category category);
}
