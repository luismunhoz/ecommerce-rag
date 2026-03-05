package com.ecommerce.domain.model;

import com.ecommerce.domain.exception.InvalidOrderException;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String orderNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<OrderItem> items = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private OrderStatus status = OrderStatus.PENDING;

    @Column(nullable = false)
    private BigDecimal totalAmount;

    private String shippingAddress;

    private String billingAddress;

    private String paymentMethod;

    private String paymentTransactionId;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private LocalDateTime completedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (orderNumber == null) {
            orderNumber = generateOrderNumber();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    private String generateOrderNumber() {
        return "ORD-" + System.currentTimeMillis();
    }

    public void addItem(OrderItem item) {
        items.add(item);
        item.setOrder(this);
        recalculateTotal();
    }

    public void removeItem(OrderItem item) {
        items.remove(item);
        item.setOrder(null);
        recalculateTotal();
    }

    public void recalculateTotal() {
        this.totalAmount = items.stream()
                .map(OrderItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public void confirm() {
        if (this.status != OrderStatus.PENDING) {
            throw new InvalidOrderException("Order can only be confirmed when in PENDING status");
        }
        this.status = OrderStatus.CONFIRMED;
    }

    public void ship() {
        if (this.status != OrderStatus.CONFIRMED && this.status != OrderStatus.PROCESSING) {
            throw new InvalidOrderException("Order can only be shipped when CONFIRMED or PROCESSING");
        }
        this.status = OrderStatus.SHIPPED;
    }

    public void deliver() {
        if (this.status != OrderStatus.SHIPPED) {
            throw new InvalidOrderException("Order can only be delivered when SHIPPED");
        }
        this.status = OrderStatus.DELIVERED;
        this.completedAt = LocalDateTime.now();
    }

    public void cancel() {
        if (this.status == OrderStatus.DELIVERED || this.status == OrderStatus.CANCELLED) {
            throw new InvalidOrderException("Cannot cancel a delivered or already cancelled order");
        }
        this.status = OrderStatus.CANCELLED;
    }

    public enum OrderStatus {
        PENDING,
        CONFIRMED,
        PROCESSING,
        SHIPPED,
        DELIVERED,
        CANCELLED
    }
}
