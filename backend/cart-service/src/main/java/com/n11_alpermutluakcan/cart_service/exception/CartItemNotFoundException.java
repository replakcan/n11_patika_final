package com.n11_alpermutluakcan.cart_service.exception;

public class CartItemNotFoundException extends RuntimeException {

    public CartItemNotFoundException(Long itemId) {
        super("Cart item not found with id: " + itemId);
    }
}
