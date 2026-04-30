package com.n11_alpermutluakcan.order_service.messaging.event;

import java.math.BigDecimal;

public record PaymentItemEvent(
        Long productId,
        String productName,
        BigDecimal unitPrice,
        Integer quantity,
        BigDecimal lineTotal
) {
}
