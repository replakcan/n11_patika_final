package com.n11_alpermutluakcan.payment_service.service;

import com.n11_alpermutluakcan.payment_service.entity.Payment;
import com.n11_alpermutluakcan.payment_service.entity.PaymentStatus;
import com.n11_alpermutluakcan.payment_service.exception.PaymentNotFoundException;
import com.n11_alpermutluakcan.payment_service.messaging.event.PaymentRequestedEvent;
import com.n11_alpermutluakcan.payment_service.messaging.producer.PaymentSagaPublisher;
import com.n11_alpermutluakcan.payment_service.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentSagaService {

    private final PaymentRepository paymentRepository;
    private final IyzicoPaymentGateway iyzicoPaymentGateway;
    private final PaymentSagaPublisher paymentSagaPublisher;

    @Transactional
    public void initializePayment(PaymentRequestedEvent event) {
        Payment existingPayment = paymentRepository.findByOrderId(event.orderId()).orElse(null);
        if (existingPayment != null
                && (existingPayment.getStatus() == PaymentStatus.INITIATED
                || existingPayment.getStatus() == PaymentStatus.SUCCEEDED)) {
            log.info("Ignoring duplicate payment initialization for order {}", event.orderId());
            return;
        }

        String conversationId = UUID.randomUUID().toString();
        CheckoutFormInitializeResult result = iyzicoPaymentGateway.initializeCheckoutForm(event, conversationId);

        if (!"success".equalsIgnoreCase(result.status())
                || result.token() == null
                || result.paymentPageUrl() == null) {
            saveFailedPayment(event, conversationId, result.errorMessage());
            paymentSagaPublisher.publishPaymentFailed(event.orderId(), event.userId(), result.errorMessage());
            return;
        }

        Payment payment = existingPayment != null ? existingPayment : new Payment();
        payment.setOrderId(event.orderId());
        payment.setUserId(event.userId());
        payment.setStatus(PaymentStatus.INITIATED);
        payment.setConversationId(conversationId);
        payment.setCheckoutToken(result.token());
        payment.setPaymentPageUrl(result.paymentPageUrl());
        payment.setTotalAmount(event.totalAmount());
        payment.setFailureReason(null);
        paymentRepository.save(payment);

        paymentSagaPublisher.publishPaymentInitialized(event.orderId(), event.userId(), result.paymentPageUrl());
    }

    @Transactional
    public void handleCallback(String checkoutToken) {
        Payment payment = paymentRepository.findByCheckoutToken(checkoutToken)
                .orElseThrow(() -> new PaymentNotFoundException(checkoutToken));

        if (payment.getStatus() == PaymentStatus.SUCCEEDED) {
            log.info("Ignoring duplicate successful callback for order {}", payment.getOrderId());
            return;
        }

        CheckoutFormRetrieveResult result = iyzicoPaymentGateway.retrieveCheckoutForm(
                payment.getConversationId(),
                checkoutToken
        );

        if ("success".equalsIgnoreCase(result.status())
                && "SUCCESS".equalsIgnoreCase(result.paymentStatus())) {
            payment.setStatus(PaymentStatus.SUCCEEDED);
            payment.setPaymentId(result.paymentId());
            payment.setFailureReason(null);
            paymentRepository.save(payment);
            paymentSagaPublisher.publishPaymentSucceeded(payment.getOrderId(), payment.getUserId(), result.paymentId());
            return;
        }

        payment.setStatus(PaymentStatus.FAILED);
        payment.setFailureReason(result.errorMessage());
        paymentRepository.save(payment);
        paymentSagaPublisher.publishPaymentFailed(payment.getOrderId(), payment.getUserId(), result.errorMessage());
    }

    private void saveFailedPayment(PaymentRequestedEvent event, String conversationId, String failureReason) {
        Payment payment = paymentRepository.findByOrderId(event.orderId())
                .orElseGet(Payment::new);
        payment.setOrderId(event.orderId());
        payment.setUserId(event.userId());
        payment.setStatus(PaymentStatus.FAILED);
        payment.setConversationId(conversationId);
        payment.setCheckoutToken(null);
        payment.setPaymentPageUrl(null);
        payment.setPaymentId(null);
        payment.setTotalAmount(event.totalAmount());
        payment.setFailureReason(failureReason);
        paymentRepository.save(payment);
    }
}
