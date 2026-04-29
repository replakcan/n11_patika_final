package com.n11_alpermutluakcan.cart_service.controller;

import com.n11_alpermutluakcan.cart_service.dto.CartItemAddRequest;
import com.n11_alpermutluakcan.cart_service.dto.CartItemResponse;
import com.n11_alpermutluakcan.cart_service.dto.CartItemUpdateRequest;
import com.n11_alpermutluakcan.cart_service.dto.CartResponse;
import com.n11_alpermutluakcan.cart_service.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping({"", "/"})
    public CartResponse getCart(@AuthenticationPrincipal Jwt jwt) {
        return cartService.getCart(getUserId(jwt));
    }

    @PostMapping("/items")
    public CartItemResponse addCartItem(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody CartItemAddRequest request
    ) {
        return cartService.addCartItem(getUserId(jwt), request);
    }

    @PutMapping("/items/{itemId}")
    public CartItemResponse updateCartItem(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long itemId,
            @Valid @RequestBody CartItemUpdateRequest request
    ) {
        return cartService.updateCartItem(getUserId(jwt), itemId, request);
    }

    @DeleteMapping("/items/{itemId}")
    public void deleteCartItem(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long itemId
    ) {
        cartService.deleteCartItem(getUserId(jwt), itemId);
    }

    private String getUserId(Jwt jwt) {
        return jwt.getSubject();
    }
}
