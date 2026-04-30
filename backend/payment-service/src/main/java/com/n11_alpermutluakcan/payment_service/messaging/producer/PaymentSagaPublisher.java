package com.n11_alpermutluakcan.payment_service.messaging.producer;

import com.n11_alpermutluakcan.payment_service.messaging.config.MessagingProperties;
import com.n11_alpermutluakcan.payment_service.messaging.event.PaymentFailedEvent;
import com.n11_alpermutluakcan.payment_service.messaging.event.PaymentInitializedEvent;
import com.n11_alpermutluakcan.payment_service.messaging.event.PaymentSucceededEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class PaymentSagaPublisher {

    private final RabbitTemplate rabbitTemplate;
    private final MessagingProperties messagingProperties;

    public void publishPaymentInitialized(Long orderId, String userId, String paymentPageUrl) {
        PaymentInitializedEvent event = new PaymentInitializedEvent(
                UUID.randomUUID().toString(),
                orderId,
                userId,
                paymentPageUrl,
                LocalDateTime.now()
        );

        rabbitTemplate.convertAndSend(
                messagingProperties.getExchange(),
                messagingProperties.getPaymentInitializedRoutingKey(),
                event
        );
    }

    public void publishPaymentSucceeded(Long orderId, String userId, String paymentId) {
        PaymentSucceededEvent event = new PaymentSucceededEvent(
                UUID.randomUUID().toString(),
                orderId,
                userId,
                paymentId,
                LocalDateTime.now()
        );

        rabbitTemplate.convertAndSend(
                messagingProperties.getExchange(),
                messagingProperties.getPaymentSucceededRoutingKey(),
                event
        );
    }

    public void publishPaymentFailed(Long orderId, String userId, String reason) {
        PaymentFailedEvent event = new PaymentFailedEvent(
                UUID.randomUUID().toString(),
                orderId,
                userId,
                reason,
                LocalDateTime.now()
        );

        rabbitTemplate.convertAndSend(
                messagingProperties.getExchange(),
                messagingProperties.getPaymentFailedRoutingKey(),
                event
        );
    }
}
