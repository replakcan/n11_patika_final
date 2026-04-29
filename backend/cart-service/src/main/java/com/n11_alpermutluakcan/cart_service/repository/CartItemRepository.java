package com.n11_alpermutluakcan.cart_service.repository;

import com.n11_alpermutluakcan.cart_service.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    List<CartItem> findAllByUserIdOrderByCreatedAtAsc(String userId);

    Optional<CartItem> findByIdAndUserId(Long id, String userId);

    Optional<CartItem> findByUserIdAndProductId(String userId, Long productId);
}
