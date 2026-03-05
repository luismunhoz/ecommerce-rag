package com.ecommerce.domain.port.out;

import com.ecommerce.domain.model.ShoppingCart;

import java.util.Optional;

public interface CartRepository {

    ShoppingCart save(ShoppingCart cart);

    Optional<ShoppingCart> findByUserId(Long userId);

    void deleteByUserId(Long userId);
}
