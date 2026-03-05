package com.ecommerce.application.service;

import com.ecommerce.application.dto.CartItemRequest;
import com.ecommerce.application.dto.CartResponse;
import com.ecommerce.domain.exception.ProductNotFoundException;
import com.ecommerce.domain.exception.UserNotFoundException;
import com.ecommerce.domain.model.Product;
import com.ecommerce.domain.model.ShoppingCart;
import com.ecommerce.domain.model.User;
import com.ecommerce.domain.port.in.AddToCartUseCase;
import com.ecommerce.domain.port.out.CartRepository;
import com.ecommerce.domain.port.out.ProductRepository;
import com.ecommerce.domain.port.out.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CartService implements AddToCartUseCase {

    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    @Override
    public CartResponse addToCart(Long userId, CartItemRequest request) {
        ShoppingCart cart = getOrCreateCart(userId);

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ProductNotFoundException(request.getProductId()));

        cart.addItem(product, request.getQuantity());

        ShoppingCart savedCart = cartRepository.save(cart);
        return CartResponse.fromEntity(savedCart);
    }

    @Override
    public CartResponse updateCartItem(Long userId, Long productId, int quantity) {
        ShoppingCart cart = getOrCreateCart(userId);

        cart.updateItemQuantity(productId, quantity);

        ShoppingCart savedCart = cartRepository.save(cart);
        return CartResponse.fromEntity(savedCart);
    }

    @Override
    public CartResponse removeFromCart(Long userId, Long productId) {
        ShoppingCart cart = getOrCreateCart(userId);

        cart.removeItem(productId);

        ShoppingCart savedCart = cartRepository.save(cart);
        return CartResponse.fromEntity(savedCart);
    }

    @Override
    @Transactional(readOnly = true)
    public CartResponse getCart(Long userId) {
        ShoppingCart cart = cartRepository.findByUserId(userId)
                .orElse(ShoppingCart.builder().build());

        return CartResponse.fromEntity(cart);
    }

    @Override
    public void clearCart(Long userId) {
        cartRepository.findByUserId(userId).ifPresent(cart -> {
            cart.clear();
            cartRepository.save(cart);
        });
    }

    private ShoppingCart getOrCreateCart(Long userId) {
        return cartRepository.findByUserId(userId)
                .orElseGet(() -> {
                    User user = userRepository.findById(userId)
                            .orElseThrow(() -> new UserNotFoundException(userId));

                    ShoppingCart newCart = ShoppingCart.builder()
                            .user(user)
                            .build();

                    return cartRepository.save(newCart);
                });
    }
}
