package com.ecommerce.domain.exception;

public class UserNotFoundException extends DomainException {

    public UserNotFoundException(Long userId) {
        super("User not found with id: " + userId);
    }

    public UserNotFoundException(String email) {
        super("User not found with email: " + email);
    }
}
