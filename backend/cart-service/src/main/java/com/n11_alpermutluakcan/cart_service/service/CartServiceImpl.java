package com.n11_alpermutluakcan.cart_service.service;

import com.n11_alpermutluakcan.cart_service.client.ProductClient;
import com.n11_alpermutluakcan.cart_service.client.dto.ProductSummary;
import com.n11_alpermutluakcan.cart_service.dto.CartItemAddRequest;
import com.n11_alpermutluakcan.cart_service.dto.CartItemResponse;
import com.n11_alpermutluakcan.cart_service.dto.CartItemUpdateRequest;
import com.n11_alpermutluakcan.cart_service.dto.CartResponse;
import com.n11_alpermutluakcan.cart_service.entity.CartItem;
import com.n11_alpermutluakcan.cart_service.exception.CartItemNotFoundException;
import com.n11_alpermutluakcan.cart_service.exception.InsufficientStockException;
import com.n11_alpermutluakcan.cart_service.exception.ProductInactiveException;
import com.n11_alpermutluakcan.cart_service.mapper.CartMapper;
import com.n11_alpermutluakcan.cart_service.repository.CartItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartItemRepository cartItemRepository;
    private final ProductClient productClient;

    @Override
    public CartResponse getCart(String userId) {
        List<CartItem> cartItems = cartItemRepository.findAllByUserIdOrderByCreatedAtAsc(userId);
        return CartMapper.toCartResponse(userId, cartItems);
    }

    @Override
    public CartItemResponse addCartItem(String userId, CartItemAddRequest request) {
        CartItem cartItem = cartItemRepository.findByUserIdAndProductId(userId, request.productId())
                .map(existingItem -> {
                    int requestedQuantity = existingItem.getQuantity() + request.quantity();
                    validateProductAvailability(request.productId(), requestedQuantity);
                    existingItem.setQuantity(requestedQuantity);
                    return existingItem;
                })
                .orElseGet(() -> CartItem.builder()
                        .userId(userId)
                        .productId(request.productId())
                        .quantity(request.quantity())
                        .build());

        if (cartItem.getId() == null) {
            validateProductAvailability(request.productId(), request.quantity());
        }

        CartItem savedItem = cartItemRepository.save(cartItem);
        return CartMapper.toResponse(savedItem);
    }

    @Override
    public CartItemResponse updateCartItem(String userId, Long itemId, CartItemUpdateRequest request) {
        CartItem cartItem = findCartItem(itemId, userId);
        validateProductAvailability(cartItem.getProductId(), request.quantity());
        cartItem.setQuantity(request.quantity());

        CartItem updatedItem = cartItemRepository.save(cartItem);
        return CartMapper.toResponse(updatedItem);
    }

    @Override
    public void deleteCartItem(String userId, Long itemId) {
        CartItem cartItem = findCartItem(itemId, userId);
        cartItemRepository.delete(cartItem);
    }

    private CartItem findCartItem(Long itemId, String userId) {
        return cartItemRepository.findByIdAndUserId(itemId, userId)
                .orElseThrow(() -> new CartItemNotFoundException(itemId));
    }

    private void validateProductAvailability(Long productId, Integer requestedQuantity) {
        ProductSummary product = productClient.getProductById(productId);

        if (!Boolean.TRUE.equals(product.active())) {
            throw new ProductInactiveException(productId);
        }

        if (product.stock() == null || requestedQuantity > product.stock()) {
            throw new InsufficientStockException(productId, requestedQuantity, product.stock());
        }
    }
}
