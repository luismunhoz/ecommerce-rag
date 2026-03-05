package com.ecommerce.domain.model;

import com.ecommerce.domain.exception.InsufficientStockException;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(length = 2000)
    private String description;

    @Column(nullable = false)
    private BigDecimal price;

    @Column(nullable = false)
    private Integer stockQuantity;

    @Column(unique = true)
    private String sku;

    private String imageUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @Column(nullable = false)
    @Builder.Default
    private boolean active = true;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public boolean isInStock() {
        return stockQuantity != null && stockQuantity > 0;
    }

    public boolean hasStock(int quantity) {
        return stockQuantity != null && stockQuantity >= quantity;
    }

    public void decreaseStock(int quantity) {
        if (!hasStock(quantity)) {
            throw new InsufficientStockException(
                    "Insufficient stock for product: " + name + ". Available: " + stockQuantity + ", Requested: " + quantity
            );
        }
        this.stockQuantity -= quantity;
    }

    public void increaseStock(int quantity) {
        if (quantity < 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
        this.stockQuantity += quantity;
    }

    public BigDecimal calculateTotal(int quantity) {
        return price.multiply(BigDecimal.valueOf(quantity));
    }
}
