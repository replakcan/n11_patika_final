package com.n11_alpermutluakcan.payment_service.service;

import com.n11_alpermutluakcan.payment_service.messaging.event.PaymentRequestedEvent;

public interface IyzicoPaymentGateway {

    CheckoutFormInitializeResult initializeCheckoutForm(PaymentRequestedEvent event, String conversationId);

    CheckoutFormRetrieveResult retrieveCheckoutForm(String conversationId, String checkoutToken);
}
