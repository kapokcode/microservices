package com.kapok.payment;

import com.kapok.clients.customer.CustomerCheckResponse;
import com.kapok.clients.customer.CustomerClient;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;


class PaymentServiceTest {
    @Mock
    private PaymentRepository paymentRepository;
    @Mock
    private CustomerClient customerClient;
    @Mock
    private CardPaymentCharger cardPaymentCharger;

    private PaymentService underTest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        underTest = new PaymentService(
                paymentRepository,
                customerClient,
                cardPaymentCharger);
    }

    @Test
    void itShouldChargeCardSuccessfully() {
        // Given
        UUID customerId = UUID.randomUUID();
        // ... Customer exists
        given(customerClient.checkCustomerExist(customerId)).willReturn(new CustomerCheckResponse(true));


        // ... Payment request
        PaymentRequest paymentRequest = new PaymentRequest(
                customerId,
                new BigDecimal("100.00"),
                Currency.RMB,
                "kapokcard",
                "donation"
        );

        // ... Card is Charged successfully
        given(cardPaymentCharger.chargeCard(
                paymentRequest.source(),
                paymentRequest.amount(),
                paymentRequest.currency(),
                paymentRequest.description()
        )).willReturn(new CardPaymentCharge(true));

        // When
        underTest.chargeCard(paymentRequest);
        // Then

        ArgumentCaptor<Payment> paymentArgumentCaptor = ArgumentCaptor.forClass(Payment.class);

        then(paymentRepository).should().save(paymentArgumentCaptor.capture());

        Payment paymentArgumentCaptorValue = paymentArgumentCaptor.getValue();

        assertThat(paymentArgumentCaptorValue).usingRecursiveComparison().ignoringFields("id").isEqualTo(paymentRequest);

    }

    @Test
    void itShouldThrowWhenCardIsNotCharged() {
        // Given
        UUID customerId = UUID.randomUUID();

        // ... Customer exists
        given(customerClient.checkCustomerExist(customerId)).willReturn(new CustomerCheckResponse(true));

        // ... Payment request
        PaymentRequest paymentRequest = new PaymentRequest(
                customerId,
                new BigDecimal("100.00"),
                Currency.RMB,
                "kapokcard",
                "donation"
        );

        // ... Card is not charged successfully
        given(cardPaymentCharger.chargeCard(
                paymentRequest.source(),
                paymentRequest.amount(),
                paymentRequest.currency(),
                paymentRequest.description()
        )).willReturn(new CardPaymentCharge(false));

        // When
        // Then
        assertThatThrownBy(() -> underTest.chargeCard(paymentRequest))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Card not debited for customer " + customerId);

        // ... No interaction with paymentRepository
        then(paymentRepository).shouldHaveNoInteractions();
    }

    @Test
    void itShouldNotChargeCardAndThrowWhenCurrencyNotSupported() {
        // Given
        UUID customerId = UUID.randomUUID();

        // ... Customer exists
        given(customerClient.checkCustomerExist(customerId)).willReturn(new CustomerCheckResponse(true));

        // ... Euros
        Currency currency = Currency.USD;

        // ... Payment request
        PaymentRequest paymentRequest = new PaymentRequest(
                customerId,
                new BigDecimal("100.00"),
                currency,
                "kapokcard",
                "donation"
        );

        // When
        assertThatThrownBy(() -> underTest.chargeCard(paymentRequest))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Currency [" + currency + "] not supported");

        // Then

        // ... No interaction with cardPaymentCharger
        then(cardPaymentCharger).shouldHaveNoInteractions();

        // ... No interaction with paymentRepository
        then(paymentRepository).shouldHaveNoInteractions();
    }

    @Test
    void itShouldNotChargeAndThrowWhenCustomerNotFound() {
        // Given
        UUID customerId = UUID.randomUUID();

        // Customer not found in db
        given(customerClient.checkCustomerExist(customerId)).willReturn(new CustomerCheckResponse(false));

        // When
        // Then
        assertThatThrownBy(() -> underTest.chargeCard(new PaymentRequest(customerId,null,null,null,null)))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Customer with id [" + customerId + "] not found");

        // ... No interactions with PaymentCharger not PaymentRepository
        then(cardPaymentCharger).shouldHaveNoInteractions();
        then(paymentRepository).shouldHaveNoInteractions();
    }
}