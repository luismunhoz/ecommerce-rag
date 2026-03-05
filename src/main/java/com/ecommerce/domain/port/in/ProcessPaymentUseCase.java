package com.ecommerce.domain.port.in;

import com.ecommerce.application.dto.PaymentRequest;
import com.ecommerce.application.dto.PaymentResponse;

public interface ProcessPaymentUseCase {

    PaymentResponse processPayment(Long orderId, PaymentRequest request);

    PaymentResponse refundPayment(Long orderId, String reason);
}
