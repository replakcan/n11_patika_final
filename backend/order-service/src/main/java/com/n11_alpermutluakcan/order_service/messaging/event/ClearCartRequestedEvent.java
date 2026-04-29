package com.n11_alpermutluakcan.order_service.messaging.event;

import java.time.LocalDateTime;

public record ClearCartRequestedEvent(
        String eventId,
        Long orderId,
        String userId,
        LocalDateTime occurredAt
) {
}
