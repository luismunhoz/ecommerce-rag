package com.ecommerce.infrastructure.adapter.out.persistence;

import com.ecommerce.domain.model.Category;
import com.ecommerce.domain.port.out.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CategoryRepositoryAdapter implements CategoryRepository {

    private final CategoryJpaRepository jpaRepository;

    @Override
    public Category save(Category category) {
        return jpaRepository.save(category);
    }

    @Override
    public Optional<Category> findById(Long id) {
        return jpaRepository.findById(id);
    }

    @Override
    public Optional<Category> findBySlug(String slug) {
        return jpaRepository.findBySlug(slug);
    }

    @Override
    public List<Category> findAll() {
        return jpaRepository.findAll();
    }

    @Override
    public List<Category> findByParentIsNull() {
        return jpaRepository.findByParentIsNull();
    }

    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public boolean existsById(Long id) {
        return jpaRepository.existsById(id);
    }

    @Override
    public boolean existsBySlug(String slug) {
        return jpaRepository.existsBySlug(slug);
    }
}
