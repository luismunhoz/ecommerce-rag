package com.ecommerce.application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductSyncMessage {

    /**
     * Operation to perform. Must be one of: CREATE, UPDATE, DELETE.
     */
    private String action;

    /**
     * Unique product identifier. Used as the key for UPDATE and DELETE operations.
     * Required for all actions.
     */
    private String sku;

    // --- Fields used by CREATE and UPDATE ---

    private String name;

    private String description;

    private BigDecimal price;

    private Integer stockQuantity;

    private String imageUrl;

    private Long categoryId;

    private Boolean active;
}
