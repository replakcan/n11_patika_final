package com.n11_alpermutluakcan.payment_service.messaging.event;

import java.time.LocalDateTime;

public record PaymentInitializedEvent(
        String eventId,
        Long orderId,
        String userId,
        String paymentPageUrl,
        LocalDateTime occurredAt
) {
}
