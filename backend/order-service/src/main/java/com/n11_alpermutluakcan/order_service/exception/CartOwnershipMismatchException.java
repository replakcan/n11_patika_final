package com.n11_alpermutluakcan.order_service.exception;

public class CartOwnershipMismatchException extends RuntimeException {

    public CartOwnershipMismatchException(String expectedUserId, String actualUserId) {
        super("Cart belongs to a different user. Expected user id: "
                + expectedUserId
                + ", actual user id: "
                + actualUserId);
    }
}
