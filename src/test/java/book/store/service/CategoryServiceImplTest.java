package book.store.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import book.store.dto.category.CategoryDto;
import book.store.dto.category.CreateCategoryRequestDto;
import book.store.exception.EntityNotFoundException;
import book.store.mapper.CategoryMapper;
import book.store.model.Category;
import book.store.repository.category.CategoryRepository;
import book.store.service.impl.CategoryServiceImpl;
import java.util.List;
import java.util.Optional;
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
public class CategoryServiceImplTest {
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private CategoryMapper categoryMapper;
    @InjectMocks
    private CategoryServiceImpl categoryServiceImpl;

    @Test
    @DisplayName("Verify findAll() method works")
    public void findAll_ValidPageable_ReturnsAllCategories() {
        //given
        Long categoryId = 1L;
        Category category = createCategory(categoryId);
        CategoryDto categoryDto = createCategoryDto(category);
        Pageable pageable = PageRequest.of(0, 10);
        List<Category> categories = List.of(category);
        Page<Category> categoryPage = new PageImpl<>(categories, pageable, categories.size());

        Mockito.when(categoryRepository.findAll(pageable)).thenReturn(categoryPage);
        Mockito.when(categoryMapper.toDto(category)).thenReturn(categoryDto);

        //when
        List<CategoryDto> actualCategoryDtos = categoryServiceImpl.findAll(pageable);

        //then
        assertThat(actualCategoryDtos).hasSize(categories.size());
        assertEquals(categoryDto, actualCategoryDtos.get(0));

        verify(categoryRepository, Mockito.times(1)).findAll(pageable);
        verify(categoryMapper, Mockito.times(categories.size())).toDto(any());
    }

    @Test
    @DisplayName("Verify getById() method works for valid categoryId")
    public void getById_WithValidCategoryId_ReturnsValidCategory() {
        // given
        Long categoryId = 1L;
        Category category = createCategory(categoryId);
        CategoryDto categoryDto = createCategoryDto(category);

        Mockito.when(categoryRepository.findById(anyLong())).thenReturn(Optional.of(category));
        Mockito.when(categoryMapper.toDto(category)).thenReturn(categoryDto);

        // when
        CategoryDto actualCategoryDto = categoryServiceImpl.getById(categoryId);

        // then
        assertEquals(categoryDto, actualCategoryDto);

        verify(categoryRepository, Mockito.times(1)).findById(anyLong());
        verify(categoryMapper, Mockito.times(1)).toDto(category);
        verifyNoMoreInteractions(categoryMapper);
        verifyNoMoreInteractions(categoryRepository);
    }

    @Test
    @DisplayName("Verify getById() method throws the EntityNotFoundException "
            + "for invalid categoryId")
    public void getById_WithInvalidCategoryId_ThrowsException() {
        // given
        Long categoryId = -1L;

        Mockito.when(categoryRepository.findById(categoryId)).thenThrow(
                new EntityNotFoundException("Can't find category with id " + categoryId));

        //when
        Exception exception = assertThrows(
                EntityNotFoundException.class,
                () -> categoryServiceImpl.getById(categoryId));

        //then
        String expectedMessage = "Can't find category with id " + categoryId;
        String actualMessage = exception.getMessage();
        assertEquals(expectedMessage, actualMessage);

        verify(categoryRepository, Mockito.times(1)).findById(anyLong());
        verifyNoMoreInteractions(categoryRepository);
    }

    @Test
    @DisplayName("Verify save() method returns the correct CategoryDto for valid Category")
    public void save_ValidCategory_ReturnsValidCategoryDto() {
        // given
        CreateCategoryRequestDto createCategoryRequestDto = new CreateCategoryRequestDto()
                .setName("Poems")
                .setDescription("Book with poems");
        Long categoryId = 1L;
        Category category = new Category()
                .setId(categoryId)
                .setName(createCategoryRequestDto.getName())
                .setDescription(createCategoryRequestDto.getDescription())
                .setDeleted(false);
        CategoryDto categoryDto = new CategoryDto()
                .setId(category.getId())
                .setName(category.getName())
                .setDescription(category.getDescription());

        Mockito.when(categoryMapper.toEntity(createCategoryRequestDto)).thenReturn(category);
        Mockito.when(categoryRepository.save(category)).thenReturn(category);
        Mockito.when(categoryMapper.toDto(category)).thenReturn(categoryDto);

        //when
        CategoryDto actualSaveCategoryDto = categoryServiceImpl.save(createCategoryRequestDto);

        //then
        assertEquals(categoryDto, actualSaveCategoryDto);

        verify(categoryMapper, Mockito.times(1)).toEntity(createCategoryRequestDto);
        verify(categoryRepository, Mockito.times(1)).save(category);
        verify(categoryMapper, Mockito.times(1)).toDto(category);
        verifyNoMoreInteractions(categoryMapper);
        verifyNoMoreInteractions(categoryRepository);
    }

    @Test
    @DisplayName("Verify update() method works for valid CategoryId")
    public void update_ValidCategoryId_ReturnsUpdatedCategoryDto() {
        //given
        Long categoryId = 1L;
        Category category = createCategory(categoryId);
        CreateCategoryRequestDto createCategoryRequestDto = new CreateCategoryRequestDto()
                .setName("name")
                .setDescription("description");
        Category updatedCategory = category
                .setId(category.getId())
                .setName(createCategoryRequestDto.getName())
                .setDescription(createCategoryRequestDto.getDescription())
                .setDeleted(category.isDeleted());
        CategoryDto updatedCategoryDto = new CategoryDto()
                .setId(updatedCategory.getId())
                .setName(updatedCategory.getName())
                .setDescription(updatedCategory.getDescription());

        Mockito.when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        Mockito.when(categoryMapper.updateCategoryFromRequestDto(
                createCategoryRequestDto, category)).thenReturn(updatedCategory);
        Mockito.when(categoryRepository.save(updatedCategory)).thenReturn(updatedCategory);
        Mockito.when(categoryMapper.toDto(updatedCategory)).thenReturn(updatedCategoryDto);

        //when
        CategoryDto actualUpdatedCategoryDto = categoryServiceImpl.update(
                categoryId, createCategoryRequestDto);

        //then
        assertEquals(updatedCategoryDto, actualUpdatedCategoryDto);

        verify(categoryRepository, Mockito.times(1)).findById(anyLong());
        verify(categoryMapper, Mockito.times(1))
                .updateCategoryFromRequestDto(createCategoryRequestDto, category);
        verify(categoryRepository, Mockito.times(1)).save(updatedCategory);
        verify(categoryMapper, Mockito.times(1)).toDto(updatedCategory);
        verifyNoMoreInteractions(categoryRepository);
        verifyNoMoreInteractions(categoryMapper);
    }

    @Test
    @DisplayName("Verify update() method throws the EntityNotFoundException for invalid categoryId")
    public void update_InvalidCategoryId_ThrowsException() {
        //given
        Long categoryId = -1L;
        CreateCategoryRequestDto createCategoryRequestDto = new CreateCategoryRequestDto()
                .setName("name")
                .setDescription("description");

        Mockito.when(categoryRepository.findById(categoryId)).thenThrow(
                new EntityNotFoundException("Can't find category with id " + categoryId)
        );

        //when
        Exception exception = assertThrows(
                EntityNotFoundException.class,
                () -> categoryServiceImpl.update(categoryId, createCategoryRequestDto)
        );

        //then
        String expectedMessage = "Can't find category with id " + categoryId;
        String actualMessage = exception.getMessage();
        assertEquals(expectedMessage, actualMessage);

        verify(categoryRepository, Mockito.times(1)).findById(anyLong());
        verifyNoMoreInteractions(categoryRepository);
    }

    @Test
    @DisplayName("Verify deleteById() method works")
    public void deleteById_ValidCategoryId_WorksOnlyOnce() {
        //given
        Long categoryId = 1L;

        //when
        categoryServiceImpl.deleteById(categoryId);

        //then
        verify(categoryRepository, Mockito.times(1)).deleteById(anyLong());
        verifyNoMoreInteractions(categoryRepository);
    }

    private Category createCategory(Long categoryId) {
        return new Category()
                .setId(categoryId)
                .setName("Poems")
                .setDescription("Book with poems")
                .setDeleted(false);
    }

    private CategoryDto createCategoryDto(@NotNull Category category) {
        return new CategoryDto()
                .setId(category.getId())
                .setName(category.getName())
                .setDescription(category.getDescription());
    }
}
