package com.kapok.payment;

import java.math.BigDecimal;
import java.util.UUID;

public record PaymentRequest (
        UUID customerId,
        BigDecimal amount,
        Currency currency,
        String source,
        String description
){
}
