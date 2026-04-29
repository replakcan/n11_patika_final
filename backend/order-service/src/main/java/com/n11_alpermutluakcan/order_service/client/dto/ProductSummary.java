package com.n11_alpermutluakcan.order_service.client.dto;

import java.math.BigDecimal;

public record ProductSummary(
        Long id,
        String name,
        String description,
        BigDecimal price,
        Integer stock,
        String imageUrl,
        Boolean active
) {
}
