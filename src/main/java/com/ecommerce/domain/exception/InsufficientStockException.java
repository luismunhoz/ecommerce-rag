package com.ecommerce.domain.exception;

public class InsufficientStockException extends DomainException {

    public InsufficientStockException(String message) {
        super(message);
    }

    public InsufficientStockException(Long productId, int requested, int available) {
        super(String.format(
                "Insufficient stock for product %d. Requested: %d, Available: %d",
                productId, requested, available
        ));
    }
}
