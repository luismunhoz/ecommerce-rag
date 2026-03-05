package com.ecommerce.infrastructure.adapter.out.persistence;

import com.ecommerce.domain.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryJpaRepository extends JpaRepository<Category, Long> {

    Optional<Category> findBySlug(String slug);

    List<Category> findByParentIsNull();

    boolean existsBySlug(String slug);
}
