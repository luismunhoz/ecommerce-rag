package com.ecommerce.infrastructure.adapter.out.persistence;

import com.ecommerce.domain.model.ShoppingCart;
import com.ecommerce.domain.port.out.CartRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CartRepositoryAdapter implements CartRepository {

    private final CartJpaRepository jpaRepository;

    @Override
    public ShoppingCart save(ShoppingCart cart) {
        return jpaRepository.save(cart);
    }

    @Override
    public Optional<ShoppingCart> findByUserId(Long userId) {
        return jpaRepository.findByUserId(userId);
    }

    @Override
    public void deleteByUserId(Long userId) {
        jpaRepository.deleteByUserId(userId);
    }
}
