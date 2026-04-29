package com.n11_alpermutluakcan.cart_service.service;

import com.n11_alpermutluakcan.cart_service.dto.CartItemAddRequest;
import com.n11_alpermutluakcan.cart_service.dto.CartItemResponse;
import com.n11_alpermutluakcan.cart_service.dto.CartItemUpdateRequest;
import com.n11_alpermutluakcan.cart_service.dto.CartResponse;

public interface CartService {

    CartResponse getCart(String userId);

    CartItemResponse addCartItem(String userId, CartItemAddRequest request);

    CartItemResponse updateCartItem(String userId, Long itemId, CartItemUpdateRequest request);

    void deleteCartItem(String userId, Long itemId);

    void clearCart(String userId);
}
