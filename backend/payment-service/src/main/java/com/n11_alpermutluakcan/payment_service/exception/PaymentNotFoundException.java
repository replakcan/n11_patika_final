package com.n11_alpermutluakcan.payment_service.exception;

public class PaymentNotFoundException extends RuntimeException {

    public PaymentNotFoundException(String token) {
        super("Payment not found for checkout token: " + token);
    }
}
