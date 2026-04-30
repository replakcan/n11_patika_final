package com.n11_alpermutluakcan.order_service.messaging.event;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record PaymentRequestedEvent(
        String eventId,
        Long orderId,
        String userId,
        BigDecimal totalAmount,
        List<PaymentItemEvent> items,
        LocalDateTime occurredAt
) {
}
