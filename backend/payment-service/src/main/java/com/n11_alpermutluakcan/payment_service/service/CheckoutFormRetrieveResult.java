package com.n11_alpermutluakcan.payment_service.service;

public record CheckoutFormRetrieveResult(
        String status,
        String paymentStatus,
        String paymentId,
        String errorMessage
) {
}
