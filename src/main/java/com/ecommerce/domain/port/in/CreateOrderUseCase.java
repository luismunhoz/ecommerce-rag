package com.ecommerce.domain.port.in;

import com.ecommerce.application.dto.CreateOrderRequest;
import com.ecommerce.application.dto.OrderResponse;

public interface CreateOrderUseCase {

    OrderResponse createOrder(Long userId, CreateOrderRequest request);

    OrderResponse createOrderFromCart(Long userId, String shippingAddress, String paymentMethod);
}
