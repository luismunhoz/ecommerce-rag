package com.ecommerce.application.dto;

import com.ecommerce.domain.model.ShoppingCart;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartResponse {

    private Long id;
    private List<CartItemResponse> items;
    private BigDecimal totalAmount;
    private int totalItems;

    public static CartResponse fromEntity(ShoppingCart cart) {
        return CartResponse.builder()
                .id(cart.getId())
                .items(cart.getItems().stream()
                        .map(CartItemResponse::fromEntity)
                        .toList())
                .totalAmount(cart.getTotalAmount())
                .totalItems(cart.getTotalItems())
                .build();
    }
}
