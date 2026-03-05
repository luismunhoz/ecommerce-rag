package com.ecommerce.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponse {

    private boolean success;
    private String transactionId;
    private String message;
    private BigDecimal amount;
    private String currency;
}
