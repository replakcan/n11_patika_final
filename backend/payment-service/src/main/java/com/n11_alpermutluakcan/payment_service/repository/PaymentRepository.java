package com.n11_alpermutluakcan.payment_service.repository;

import com.n11_alpermutluakcan.payment_service.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByOrderId(Long orderId);

    Optional<Payment> findByCheckoutToken(String checkoutToken);
}
