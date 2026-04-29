package com.n11_alpermutluakcan.cart_service.client.dto;

public record ProductSummary(
        Long id,
        Integer stock,
        Boolean active
) {
}
