package com.ecommerce.domain.port.out;

import java.math.BigDecimal;

public interface PaymentGateway {

    PaymentResult processPayment(String paymentMethodId, BigDecimal amount, String currency);

    PaymentResult refund(String transactionId, BigDecimal amount, String reason);

    record PaymentResult(
            boolean success,
            String transactionId,
            String message,
            String errorCode
    ) {
        public static PaymentResult success(String transactionId) {
            return new PaymentResult(true, transactionId, "Payment successful", null);
        }

        public static PaymentResult failure(String message, String errorCode) {
            return new PaymentResult(false, null, message, errorCode);
        }
    }
}
