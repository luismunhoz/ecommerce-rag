package com.ecommerce.infrastructure.adapter.out.persistence;

import com.ecommerce.domain.model.ShoppingCart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartJpaRepository extends JpaRepository<ShoppingCart, Long> {

    @Query("SELECT c FROM ShoppingCart c LEFT JOIN FETCH c.items i LEFT JOIN FETCH i.product WHERE c.user.id = :userId")
    Optional<ShoppingCart> findByUserId(@Param("userId") Long userId);

    void deleteByUserId(Long userId);
}
