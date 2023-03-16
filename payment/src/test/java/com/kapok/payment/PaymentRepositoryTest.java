package com.kapok.payment;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(
        properties = {
                "spring.jpa.properties.javax.persistence.validation.mode=none"
        }
)
class PaymentRepositoryTest {
    @Autowired
    PaymentRepository underTest;

    @Test
    void itShouldInsertPayment() {
        // Given
        Long paymentId = 1L;
        Payment payment = new Payment(
                paymentId,
                UUID.randomUUID(),
                new BigDecimal("10.00"),
                Currency.RMB,
                "kapokCard",
                "donation");
        // When
        underTest.save(payment);

        // Then
        Optional<Payment> paymentOptional = underTest.findById(paymentId);

        assertThat(paymentOptional)
                .hasValueSatisfying(p -> assertThat(p).usingRecursiveComparison().isEqualTo(payment));
    }
}