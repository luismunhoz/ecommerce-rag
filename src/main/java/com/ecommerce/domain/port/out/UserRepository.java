package com.ecommerce.domain.port.out;

import com.ecommerce.domain.model.User;

import java.util.Optional;

public interface UserRepository {

    User save(User user);

    Optional<User> findById(Long id);

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    void deleteById(Long id);
}
