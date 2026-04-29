package com.n11_alpermutluakcan.cart_service.messaging.event;

import java.time.LocalDateTime;

public record CartClearedEvent(
        String eventId,
        Long orderId,
        String userId,
        LocalDateTime occurredAt
) {
}
