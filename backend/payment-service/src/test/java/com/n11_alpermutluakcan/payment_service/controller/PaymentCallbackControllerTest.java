package com.n11_alpermutluakcan.payment_service.controller;

import com.n11_alpermutluakcan.payment_service.config.IyzicoProperties;
import com.n11_alpermutluakcan.payment_service.service.PaymentSagaService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentCallbackControllerTest {

    @Test
    void shouldRedirectToOrdersPageAfterSuccessfulCallback() {
        PaymentSagaService paymentSagaService = mock(PaymentSagaService.class);
        IyzicoProperties iyzicoProperties = new IyzicoProperties();
        iyzicoProperties.setFrontendBaseUrl("http://localhost:3000/");
        PaymentCallbackController controller = new PaymentCallbackController(paymentSagaService, iyzicoProperties);

        when(paymentSagaService.handleCallback("checkout-token")).thenReturn(42L);

        ResponseEntity<String> response = controller.handlePostCallback("checkout-token");

        assertThat(response.getBody()).contains("http://localhost:3000/orders?payment=confirmed&orderId=42");
        assertThat(response.getBody()).doesNotContain("http://localhost:3000/orders/42");
        verify(paymentSagaService).handleCallback("checkout-token");
    }
}
