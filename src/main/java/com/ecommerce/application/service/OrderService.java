package com.ecommerce.application.service;

import com.ecommerce.application.dto.CreateOrderRequest;
import com.ecommerce.application.dto.OrderResponse;
import com.ecommerce.domain.exception.InvalidOrderException;
import com.ecommerce.domain.exception.OrderNotFoundException;
import com.ecommerce.domain.exception.UserNotFoundException;
import com.ecommerce.domain.model.*;
import com.ecommerce.domain.port.in.CreateOrderUseCase;
import com.ecommerce.domain.port.out.CartRepository;
import com.ecommerce.domain.port.out.OrderRepository;
import com.ecommerce.domain.port.out.ProductRepository;
import com.ecommerce.domain.port.out.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService implements CreateOrderUseCase {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final CartRepository cartRepository;

    @Override
    public OrderResponse createOrder(Long userId, CreateOrderRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        Order order = Order.builder()
                .user(user)
                .shippingAddress(request.getShippingAddress())
                .billingAddress(request.getBillingAddress() != null
                        ? request.getBillingAddress()
                        : request.getShippingAddress())
                .paymentMethod(request.getPaymentMethod())
                .totalAmount(BigDecimal.ZERO)
                .build();

        for (var itemRequest : request.getItems()) {
            Product product = productRepository.findById(itemRequest.getProductId())
                    .orElseThrow(() -> new InvalidOrderException(
                            "Product not found: " + itemRequest.getProductId()));

            product.decreaseStock(itemRequest.getQuantity());
            productRepository.save(product);

            OrderItem orderItem = OrderItem.create(product, itemRequest.getQuantity());
            order.addItem(orderItem);
        }

        Order savedOrder = orderRepository.save(order);
        return OrderResponse.fromEntity(savedOrder);
    }

    @Override
    public OrderResponse createOrderFromCart(Long userId, String shippingAddress, String paymentMethod) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        ShoppingCart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new InvalidOrderException("Cart is empty"));

        if (cart.isEmpty()) {
            throw new InvalidOrderException("Cart is empty");
        }

        Order order = Order.builder()
                .user(user)
                .shippingAddress(shippingAddress)
                .billingAddress(shippingAddress)
                .paymentMethod(paymentMethod)
                .totalAmount(BigDecimal.ZERO)
                .build();

        for (CartItem cartItem : cart.getItems()) {
            Product product = cartItem.getProduct();
            product.decreaseStock(cartItem.getQuantity());
            productRepository.save(product);

            OrderItem orderItem = OrderItem.create(product, cartItem.getQuantity());
            order.addItem(orderItem);
        }

        Order savedOrder = orderRepository.save(order);

        cart.clear();
        cartRepository.save(cart);

        return OrderResponse.fromEntity(savedOrder);
    }

    @Transactional(readOnly = true)
    public OrderResponse getOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));
        return OrderResponse.fromEntity(order);
    }

    @Transactional(readOnly = true)
    public OrderResponse getOrderByNumber(String orderNumber) {
        Order order = orderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new OrderNotFoundException(orderNumber));
        return OrderResponse.fromEntity(order);
    }

    @Transactional(readOnly = true)
    public Page<OrderResponse> getUserOrders(Long userId, Pageable pageable) {
        return orderRepository.findByUserId(userId, pageable)
                .map(OrderResponse::fromEntity);
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> getUserOrders(Long userId) {
        return orderRepository.findByUserId(userId).stream()
                .map(OrderResponse::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public Page<OrderResponse> getAllOrders(Pageable pageable) {
        return orderRepository.findAll(pageable)
                .map(OrderResponse::fromEntity);
    }

    public OrderResponse confirmOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));
        order.confirm();
        return OrderResponse.fromEntity(orderRepository.save(order));
    }

    public OrderResponse shipOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));
        order.ship();
        return OrderResponse.fromEntity(orderRepository.save(order));
    }

    public OrderResponse deliverOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));
        order.deliver();
        return OrderResponse.fromEntity(orderRepository.save(order));
    }

    public OrderResponse cancelOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));

        for (OrderItem item : order.getItems()) {
            Product product = item.getProduct();
            product.increaseStock(item.getQuantity());
            productRepository.save(product);
        }

        order.cancel();
        return OrderResponse.fromEntity(orderRepository.save(order));
    }
}
