package com.ecommerce.domain.port.out;

import com.ecommerce.domain.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface ProductRepository {

    Product save(Product product);

    Optional<Product> findById(Long id);

    Optional<Product> findBySku(String sku);

    List<Product> findAll();

    List<Product> findAllById(List<Long> ids);

    Page<Product> findAll(Pageable pageable);

    Page<Product> findByCategory(Long categoryId, Pageable pageable);

    Page<Product> findByNameContainingIgnoreCase(String name, Pageable pageable);

    List<Product> findByActiveTrue();

    void deleteById(Long id);

    boolean existsById(Long id);

    boolean existsBySku(String sku);
}
