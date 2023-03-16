package com.kapok.clients.customer;


import com.kapok.clients.fraud.FraudCheckResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(
        name = "customer",
        url = "${clients.customer.url}"
)
public interface CustomerClient {

    @GetMapping(path = "api/v1/customers/{customerId}")
    CustomerCheckResponse checkCustomerExist(
            @PathVariable("customerId") UUID customerId);

}