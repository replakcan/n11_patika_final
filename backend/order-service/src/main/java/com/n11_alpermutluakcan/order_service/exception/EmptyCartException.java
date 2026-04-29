package com.n11_alpermutluakcan.order_service.exception;

public class EmptyCartException extends RuntimeException {

    public EmptyCartException() {
        super("Cannot create an order from an empty cart");
    }
}
