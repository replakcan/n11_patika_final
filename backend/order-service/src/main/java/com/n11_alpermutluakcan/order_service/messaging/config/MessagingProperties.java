package com.n11_alpermutluakcan.order_service.messaging.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "app.messaging")
public class MessagingProperties {

    private String exchange = "ecommerce.exchange";
    private String stockReservedQueue = "order.stock-reserved.queue";
    private String stockFailedQueue = "order.stock-failed.queue";
    private String cartClearedQueue = "order.cart-cleared.queue";
    private String cartClearFailedQueue = "order.cart-clear-failed.queue";
    private String paymentInitializedQueue = "order.payment-initialized.queue";
    private String paymentSucceededQueue = "order.payment-succeeded.queue";
    private String paymentFailedQueue = "order.payment-failed.queue";
    private String orderCreatedRoutingKey = "order.created";
    private String stockReservedRoutingKey = "stock.reserved";
    private String stockFailedRoutingKey = "stock.failed";
    private String cartClearRoutingKey = "cart.clear.requested";
    private String cartClearedRoutingKey = "cart.cleared";
    private String cartClearFailedRoutingKey = "cart.clear.failed";
    private String stockReleaseRoutingKey = "stock.release.requested";
    private String paymentRequestedRoutingKey = "payment.requested";
    private String paymentInitializedRoutingKey = "payment.initialized";
    private String paymentSucceededRoutingKey = "payment.succeeded";
    private String paymentFailedRoutingKey = "payment.failed";
}
