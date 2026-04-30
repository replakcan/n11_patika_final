package com.n11_alpermutluakcan.payment_service.messaging.consumer;

import com.n11_alpermutluakcan.payment_service.messaging.event.PaymentItemEvent;
import com.n11_alpermutluakcan.payment_service.messaging.event.PaymentRequestedEvent;
import com.n11_alpermutluakcan.payment_service.service.PaymentSagaService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PaymentSagaConsumerTest {

    @Mock
    private PaymentSagaService paymentSagaService;

    @InjectMocks
    private PaymentSagaConsumer paymentSagaConsumer;

    @Test
    void shouldInitializePaymentForIncomingRequest() {
        PaymentRequestedEvent event = new PaymentRequestedEvent(
                "evt-1",
                1L,
                "user-1",
                new BigDecimal("25.00"),
                List.of(new PaymentItemEvent(10L, "Keyboard", new BigDecimal("25.00"), 1, new BigDecimal("25.00"))),
                LocalDateTime.now()
        );

        paymentSagaConsumer.onPaymentRequested(event);

        verify(paymentSagaService).initializePayment(event);
    }
}
