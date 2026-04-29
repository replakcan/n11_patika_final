package com.n11_alpermutluakcan.cart_service.dto;

import java.util.List;

public record CartResponse(
        String userId,
        Integer totalItems,
        List<CartItemResponse> items
) {
}
