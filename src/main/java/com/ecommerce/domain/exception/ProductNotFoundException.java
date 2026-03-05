package com.ecommerce.domain.exception;

public class ProductNotFoundException extends DomainException {

    public ProductNotFoundException(Long productId) {
        super("Product not found with id: " + productId);
    }

    public ProductNotFoundException(String sku) {
        super("Product not found with SKU: " + sku);
    }
}
