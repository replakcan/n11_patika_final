package com.n11_alpermutluakcan.payment_service.messaging.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "app.messaging")
public class MessagingProperties {

    private String exchange = "ecommerce.exchange";
    private String paymentRequestedQueue = "payment.requested.queue";
    private String paymentInitializedRoutingKey = "payment.initialized";
    private String paymentSucceededRoutingKey = "payment.succeeded";
    private String paymentFailedRoutingKey = "payment.failed";
    private String paymentRequestedRoutingKey = "payment.requested";
}
