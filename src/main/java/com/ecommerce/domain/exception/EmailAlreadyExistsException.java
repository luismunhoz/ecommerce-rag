package com.ecommerce.domain.exception;

public class EmailAlreadyExistsException extends DomainException {

    public EmailAlreadyExistsException(String email) {
        super("Email already registered: " + email);
    }
}
