package com.n11_alpermutluakcan.cart_service.messaging.event;

import java.time.LocalDateTime;

public record CartClearFailedEvent(
        String eventId,
        Long orderId,
        String userId,
        String reason,
        LocalDateTime occurredAt
) {
}
