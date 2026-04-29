package com.n11_alpermutluakcan.order_service.client.dto;

import java.time.LocalDateTime;

public record CartItemSummary(
        Long id,
        Long productId,
        Integer quantity,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
