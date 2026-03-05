package com.ecommerce.domain.port.in;

import com.ecommerce.application.dto.CartItemRequest;
import com.ecommerce.application.dto.CartResponse;

public interface AddToCartUseCase {

    CartResponse addToCart(Long userId, CartItemRequest request);

    CartResponse updateCartItem(Long userId, Long productId, int quantity);

    CartResponse removeFromCart(Long userId, Long productId);

    CartResponse getCart(Long userId);

    void clearCart(Long userId);
}
