package com.n11_alpermutluakcan.product_service.messaging.event;

public record OrderItemEvent(
        Long productId,
        Integer quantity
) {
}
