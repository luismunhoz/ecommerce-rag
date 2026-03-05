package com.ecommerce.application.service;

import com.ecommerce.application.dto.CategoryDTO;
import com.ecommerce.domain.exception.DomainException;
import com.ecommerce.domain.model.Category;
import com.ecommerce.domain.port.out.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CategoryService {

    private final CategoryRepository categoryRepository;

    @Transactional(readOnly = true)
    public List<CategoryDTO> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(CategoryDTO::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<CategoryDTO> getRootCategories() {
        return categoryRepository.findByParentIsNull().stream()
                .map(CategoryDTO::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public CategoryDTO getCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new DomainException("Category not found: " + id));
        return CategoryDTO.fromEntity(category);
    }

    @Transactional(readOnly = true)
    public CategoryDTO getCategoryBySlug(String slug) {
        Category category = categoryRepository.findBySlug(slug)
                .orElseThrow(() -> new DomainException("Category not found: " + slug));
        return CategoryDTO.fromEntity(category);
    }

    public CategoryDTO createCategory(String name, String description, Long parentId) {
        Category parent = null;
        if (parentId != null) {
            parent = categoryRepository.findById(parentId)
                    .orElseThrow(() -> new DomainException("Parent category not found: " + parentId));
        }

        Category category = Category.builder()
                .name(name)
                .description(description)
                .parent(parent)
                .build();

        Category savedCategory = categoryRepository.save(category);
        return CategoryDTO.fromEntity(savedCategory);
    }

    public CategoryDTO updateCategory(Long id, String name, String description) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new DomainException("Category not found: " + id));

        if (name != null) {
            category.setName(name);
        }
        if (description != null) {
            category.setDescription(description);
        }

        Category updatedCategory = categoryRepository.save(category);
        return CategoryDTO.fromEntity(updatedCategory);
    }

    public void deleteCategory(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new DomainException("Category not found: " + id);
        }
        categoryRepository.deleteById(id);
    }
}
