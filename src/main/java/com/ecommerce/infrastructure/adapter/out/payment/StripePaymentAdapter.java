package com.ecommerce.infrastructure.adapter.out.payment;

import com.ecommerce.domain.port.out.PaymentGateway;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.UUID;

@Slf4j
@Component
public class StripePaymentAdapter implements PaymentGateway {

    @Override
    public PaymentResult processPayment(String paymentMethodId, BigDecimal amount, String currency) {
        log.info("Processing payment: method={}, amount={} {}", paymentMethodId, amount, currency);

        // Stub implementation - in production, integrate with Stripe API
        String transactionId = "txn_" + UUID.randomUUID().toString().replace("-", "").substring(0, 24);

        log.info("Payment processed successfully: transactionId={}", transactionId);
        return PaymentResult.success(transactionId);
    }

    @Override
    public PaymentResult refund(String transactionId, BigDecimal amount, String reason) {
        log.info("Processing refund: transactionId={}, amount={}, reason={}", transactionId, amount, reason);

        // Stub implementation - in production, integrate with Stripe API
        String refundId = "re_" + UUID.randomUUID().toString().replace("-", "").substring(0, 24);

        log.info("Refund processed successfully: refundId={}", refundId);
        return PaymentResult.success(refundId);
    }
}
