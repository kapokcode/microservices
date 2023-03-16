package com.kapok.payment;

import com.kapok.clients.customer.CustomerClient;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class PaymentService {
    private static final List<Currency> ACCEPTED_CURRENCIES = List.of(Currency.RMB);

    private final PaymentRepository paymentRepository;
    private final CustomerClient customerClient;
    private final CardPaymentCharger cardPaymentCharger;

    void chargeCard(PaymentRequest paymentRequest){
        UUID customerId = paymentRequest.customerId();
        //1. Does customer exists if not throw
        boolean isCustomerExist = customerClient.checkCustomerExist(customerId).isExist();
        if (!isCustomerExist){
            throw new IllegalStateException(String.format("Customer with id [%s] not found", customerId));
        }
        //2. Do we support the currency if not throw
        boolean isCurrencySupported = ACCEPTED_CURRENCIES.contains(paymentRequest.currency());

        if(! isCurrencySupported) {
            throw  new IllegalStateException(String.format("Currency [%s] not supported", paymentRequest.currency()));
        }
        //3. Charge card
        CardPaymentCharge cardPaymentCharge = cardPaymentCharger.chargeCard(
                paymentRequest.source(),
                paymentRequest.amount(),
                paymentRequest.currency(),
                paymentRequest.description()
        );
        //4. If not debited throw
        if(!cardPaymentCharge.isCardDebited()){
            throw  new IllegalStateException(String.format("Card not debited for customer %s", customerId));
        }
        //5. Insert payment
        Payment payment = Payment.builder()
                .customerId(paymentRequest.customerId())
                .amount(paymentRequest.amount())
                .currency(paymentRequest.currency())
                .source(paymentRequest.source())
                .description(paymentRequest.description())
                .build();

        paymentRepository.save(payment);
        //6. TODO: send sms
    }
}
