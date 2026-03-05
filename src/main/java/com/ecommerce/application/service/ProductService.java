package com.ecommerce.application.service;

import com.ecommerce.application.dto.CreateProductRequest;
import com.ecommerce.application.dto.ProductDTO;
import com.ecommerce.application.dto.UpdateProductRequest;
import com.ecommerce.domain.exception.ProductNotFoundException;
import com.ecommerce.domain.model.Category;
import com.ecommerce.domain.model.Product;
import com.ecommerce.domain.port.out.CategoryRepository;
import com.ecommerce.domain.port.out.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductEmbeddingService productEmbeddingService;

    @Transactional(readOnly = true)
    public Page<ProductDTO> getAllProducts(Pageable pageable) {
        return productRepository.findAll(pageable)
                .map(ProductDTO::fromEntity);
    }

    @Transactional(readOnly = true)
    public ProductDTO getProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
        return ProductDTO.fromEntity(product);
    }

    @Transactional(readOnly = true)
    public Page<ProductDTO> getProductsByCategory(Long categoryId, Pageable pageable) {
        return productRepository.findByCategory(categoryId, pageable)
                .map(ProductDTO::fromEntity);
    }

    @Transactional(readOnly = true)
    public Page<ProductDTO> searchProducts(String query, Pageable pageable) {
        return productRepository.findByNameContainingIgnoreCase(query, pageable)
                .map(ProductDTO::fromEntity);
    }

    @Transactional(readOnly = true)
    public List<ProductDTO> getActiveProducts() {
        return productRepository.findByActiveTrue().stream()
                .map(ProductDTO::fromEntity)
                .toList();
    }

    public ProductDTO createProduct(CreateProductRequest request) {
        Category category = null;
        if (request.getCategoryId() != null) {
            category = categoryRepository.findById(request.getCategoryId())
                    .orElse(null);
        }

        Product product = Product.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .stockQuantity(request.getStockQuantity())
                .sku(request.getSku())
                .imageUrl(request.getImageUrl())
                .category(category)
                .active(true)
                .build();

        Product savedProduct = productRepository.save(product);
        productEmbeddingService.indexProduct(savedProduct);
        return ProductDTO.fromEntity(savedProduct);
    }

    public ProductDTO updateProduct(Long id, UpdateProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));

        if (request.getName() != null) {
            product.setName(request.getName());
        }
        if (request.getDescription() != null) {
            product.setDescription(request.getDescription());
        }
        if (request.getPrice() != null) {
            product.setPrice(request.getPrice());
        }
        if (request.getStockQuantity() != null) {
            product.setStockQuantity(request.getStockQuantity());
        }
        if (request.getSku() != null) {
            product.setSku(request.getSku());
        }
        if (request.getImageUrl() != null) {
            product.setImageUrl(request.getImageUrl());
        }
        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElse(null);
            product.setCategory(category);
        }
        if (request.getActive() != null) {
            product.setActive(request.getActive());
        }

        Product updatedProduct = productRepository.save(product);
        productEmbeddingService.indexProduct(updatedProduct);
        return ProductDTO.fromEntity(updatedProduct);
    }

    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new ProductNotFoundException(id);
        }
        productRepository.deleteById(id);
        productEmbeddingService.removeProduct(id);
    }

    public Product getProductEntity(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
    }
}
