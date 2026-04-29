package com.n11_alpermutluakcan.cart_service.mapper;

import com.n11_alpermutluakcan.cart_service.dto.CartItemResponse;
import com.n11_alpermutluakcan.cart_service.dto.CartResponse;
import com.n11_alpermutluakcan.cart_service.entity.CartItem;

import java.util.List;

public final class CartMapper {

    private CartMapper() {
    }

    public static CartItemResponse toResponse(CartItem cartItem) {
        return new CartItemResponse(
                cartItem.getId(),
                cartItem.getProductId(),
                cartItem.getQuantity(),
                cartItem.getCreatedAt(),
                cartItem.getUpdatedAt()
        );
    }

    public static CartResponse toCartResponse(String userId, List<CartItem> cartItems) {
        List<CartItemResponse> items = cartItems.stream()
                .map(CartMapper::toResponse)
                .toList();

        int totalItems = cartItems.stream()
                .mapToInt(CartItem::getQuantity)
                .sum();

        return new CartResponse(userId, totalItems, items);
    }
}
