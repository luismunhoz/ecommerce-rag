package com.ecommerce.domain.exception;

public class UnauthorizedAccessException extends DomainException {

    public UnauthorizedAccessException(String message) {
        super(message);
    }

    public UnauthorizedAccessException() {
        super("You are not authorized to access this resource");
    }
}
