package com.n11_alpermutluakcan.cart_service.dto;

import java.time.LocalDateTime;

public record CartItemResponse(
        Long id,
        Long productId,
        Integer quantity,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
