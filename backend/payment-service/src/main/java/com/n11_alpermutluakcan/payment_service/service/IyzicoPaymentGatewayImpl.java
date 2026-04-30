package com.n11_alpermutluakcan.payment_service.service;

import com.iyzipay.Options;
import com.iyzipay.model.Address;
import com.iyzipay.model.BasketItem;
import com.iyzipay.model.BasketItemType;
import com.iyzipay.model.Buyer;
import com.iyzipay.model.CheckoutForm;
import com.iyzipay.model.CheckoutFormInitialize;
import com.iyzipay.model.Currency;
import com.iyzipay.model.Locale;
import com.iyzipay.model.PaymentGroup;
import com.iyzipay.request.CreateCheckoutFormInitializeRequest;
import com.iyzipay.request.RetrieveCheckoutFormRequest;
import com.n11_alpermutluakcan.payment_service.config.IyzicoProperties;
import com.n11_alpermutluakcan.payment_service.messaging.event.PaymentItemEvent;
import com.n11_alpermutluakcan.payment_service.messaging.event.PaymentRequestedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
@RequiredArgsConstructor
public class IyzicoPaymentGatewayImpl implements IyzicoPaymentGateway {

    private static final DateTimeFormatter IYZI_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final IyzicoProperties iyzicoProperties;

    @Override
    public CheckoutFormInitializeResult initializeCheckoutForm(PaymentRequestedEvent event, String conversationId) {
        CreateCheckoutFormInitializeRequest request = new CreateCheckoutFormInitializeRequest();
        request.setLocale(resolveLocale());
        request.setConversationId(conversationId);
        request.setPrice(event.totalAmount());
        request.setPaidPrice(event.totalAmount());
        request.setCurrency(resolveCurrency());
        request.setBasketId("order-" + event.orderId());
        request.setPaymentGroup(PaymentGroup.PRODUCT.name());
        request.setCallbackUrl(iyzicoProperties.getCallbackUrl());
        request.setBuyer(buildBuyer(event.userId()));
        request.setShippingAddress(buildAddress());
        request.setBillingAddress(buildAddress());
        request.setBasketItems(buildBasketItems(event.items()));

        CheckoutFormInitialize response = CheckoutFormInitialize.create(request, buildOptions());
        return new CheckoutFormInitializeResult(
                response.getStatus(),
                response.getToken(),
                response.getPaymentPageUrl(),
                response.getErrorMessage()
        );
    }

    @Override
    public CheckoutFormRetrieveResult retrieveCheckoutForm(String conversationId, String checkoutToken) {
        RetrieveCheckoutFormRequest request = new RetrieveCheckoutFormRequest();
        request.setLocale(resolveLocale());
        request.setConversationId(conversationId);
        request.setToken(checkoutToken);

        CheckoutForm response = CheckoutForm.retrieve(request, buildOptions());
        return new CheckoutFormRetrieveResult(
                response.getStatus(),
                response.getPaymentStatus(),
                response.getPaymentId(),
                response.getErrorMessage()
        );
    }

    private Options buildOptions() {
        Options options = new Options();
        options.setApiKey(iyzicoProperties.getApiKey());
        options.setSecretKey(iyzicoProperties.getSecretKey());
        options.setBaseUrl(iyzicoProperties.getBaseUrl());
        return options;
    }

    private Buyer buildBuyer(String userId) {
        Buyer buyer = new Buyer();
        buyer.setId(userId);
        buyer.setName(iyzicoProperties.getBuyerName());
        buyer.setSurname(iyzicoProperties.getBuyerSurname());
        buyer.setIdentityNumber(iyzicoProperties.getBuyerIdentityNumber());
        buyer.setEmail(iyzicoProperties.getBuyerEmail());
        buyer.setGsmNumber(iyzicoProperties.getBuyerGsmNumber());
        buyer.setRegistrationDate(LocalDateTime.now().minusDays(30).format(IYZI_DATE_FORMAT));
        buyer.setLastLoginDate(LocalDateTime.now().format(IYZI_DATE_FORMAT));
        buyer.setRegistrationAddress(iyzicoProperties.getAddress());
        buyer.setCity(iyzicoProperties.getCity());
        buyer.setCountry(iyzicoProperties.getCountry());
        buyer.setZipCode(iyzicoProperties.getZipCode());
        buyer.setIp(iyzicoProperties.getBuyerIp());
        return buyer;
    }

    private Address buildAddress() {
        Address address = new Address();
        address.setAddress(iyzicoProperties.getAddress());
        address.setZipCode(iyzicoProperties.getZipCode());
        address.setContactName(iyzicoProperties.getBuyerName() + " " + iyzicoProperties.getBuyerSurname());
        address.setCity(iyzicoProperties.getCity());
        address.setCountry(iyzicoProperties.getCountry());
        return address;
    }

    private List<BasketItem> buildBasketItems(List<PaymentItemEvent> items) {
        return items.stream()
                .map(item -> {
                    BasketItem basketItem = new BasketItem();
                    basketItem.setId(item.productId().toString());
                    basketItem.setName(item.productName());
                    basketItem.setCategory1("Products");
                    basketItem.setItemType(BasketItemType.PHYSICAL.name());
                    basketItem.setPrice(item.lineTotal());
                    return basketItem;
                })
                .toList();
    }

    private String resolveLocale() {
        return "en".equalsIgnoreCase(iyzicoProperties.getLocale())
                ? Locale.EN.getValue()
                : Locale.TR.getValue();
    }

    private String resolveCurrency() {
        return Currency.valueOf(iyzicoProperties.getCurrency().toUpperCase()).name();
    }
}
