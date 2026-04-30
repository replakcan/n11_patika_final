package com.n11_alpermutluakcan.payment_service.messaging.event;

import java.time.LocalDateTime;

public record PaymentFailedEvent(
        String eventId,
        Long orderId,
        String userId,
        String reason,
        LocalDateTime occurredAt
) {
}
