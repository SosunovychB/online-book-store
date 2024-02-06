package book.store.service.impl;

import book.store.dto.category.CategoryDto;
import book.store.dto.category.CreateCategoryRequestDto;
import book.store.exception.EntityNotFoundException;
import book.store.mapper.CategoryMapper;
import book.store.model.Category;
import book.store.repository.category.CategoryRepository;
import book.store.service.CategoryService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryMapper categoryMapper;
    private final CategoryRepository categoryRepository;

    @Override
    public List<CategoryDto> findAll(Pageable pageable) {
        return categoryRepository.findAll(pageable).stream()
                .map(categoryMapper::toDto)
                .toList();
    }

    @Override
    public CategoryDto getById(Long id) {
        return categoryRepository.findById(id)
                .map(categoryMapper::toDto)
                .orElseThrow(
                        () -> new EntityNotFoundException("Can't find category with id " + id)
                );
    }

    @Override
    public CategoryDto save(CreateCategoryRequestDto createCategoryRequestDto) {
        Category savedCategory = categoryRepository.save(
                categoryMapper.toEntity(createCategoryRequestDto));
        return categoryMapper.toDto(savedCategory);
    }

    @Override
    public CategoryDto update(Long id, CreateCategoryRequestDto createCategoryRequestDto) {
        Category categoryById = categoryRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Can't find category with id " + id)
        );
        Category updatedCategory = categoryMapper
                .updateCategoryFromRequestDto(createCategoryRequestDto, categoryById);
        categoryRepository.save(updatedCategory);
        return categoryMapper.toDto(updatedCategory);
    }

    @Override
    public void deleteById(Long id) {
        categoryRepository.deleteById(id);
    }
}
