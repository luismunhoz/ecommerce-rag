package com.ecommerce.domain.port.out;

import com.ecommerce.domain.model.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface OrderRepository {

    Order save(Order order);

    Optional<Order> findById(Long id);

    Optional<Order> findByOrderNumber(String orderNumber);

    List<Order> findByUserId(Long userId);

    Page<Order> findByUserId(Long userId, Pageable pageable);

    Page<Order> findAll(Pageable pageable);

    List<Order> findByStatus(Order.OrderStatus status);

    void deleteById(Long id);

    boolean existsById(Long id);
}
