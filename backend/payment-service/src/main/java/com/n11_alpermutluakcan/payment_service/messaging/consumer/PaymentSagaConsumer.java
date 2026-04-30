package com.n11_alpermutluakcan.payment_service.messaging.consumer;

import com.n11_alpermutluakcan.payment_service.messaging.event.PaymentRequestedEvent;
import com.n11_alpermutluakcan.payment_service.service.PaymentSagaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class PaymentSagaConsumer {

    private final PaymentSagaService paymentSagaService;

    @RabbitListener(queues = "${app.messaging.payment-requested-queue:payment.requested.queue}")
    public void onPaymentRequested(PaymentRequestedEvent event) {
        log.info("Received PaymentRequested for order {}", event.orderId());
        paymentSagaService.initializePayment(event);
    }
}
