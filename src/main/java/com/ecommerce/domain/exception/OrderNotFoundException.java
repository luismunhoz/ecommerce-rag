package com.ecommerce.domain.exception;

public class OrderNotFoundException extends DomainException {

    public OrderNotFoundException(Long orderId) {
        super("Order not found with id: " + orderId);
    }

    public OrderNotFoundException(String orderNumber) {
        super("Order not found with number: " + orderNumber);
    }
}
