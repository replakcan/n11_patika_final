package com.n11_alpermutluakcan.order_service.client.dto;

import java.util.List;

public record CartSummary(
        String userId,
        Integer totalItems,
        List<CartItemSummary> items
) {
}
