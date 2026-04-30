package com.n11_alpermutluakcan.payment_service.messaging.event;

import java.time.LocalDateTime;

public record PaymentSucceededEvent(
        String eventId,
        Long orderId,
        String userId,
        String paymentId,
        LocalDateTime occurredAt
) {
}
