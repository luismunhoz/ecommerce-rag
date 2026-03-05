package com.ecommerce.domain.port.out;

import com.ecommerce.domain.model.Category;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository {

    Category save(Category category);

    Optional<Category> findById(Long id);

    Optional<Category> findBySlug(String slug);

    List<Category> findAll();

    List<Category> findByParentIsNull();

    void deleteById(Long id);

    boolean existsById(Long id);

    boolean existsBySlug(String slug);
}
