package com.n11_alpermutluakcan.payment_service.service;

public record CheckoutFormInitializeResult(
        String status,
        String token,
        String paymentPageUrl,
        String errorMessage
) {
}
