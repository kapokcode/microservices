package com.kapok.customer;

import com.kapok.clients.customer.CustomerCheckResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("api/v1/customers")
@AllArgsConstructor
public class CustomerController {
    private final CustomerService customerService;
    @PostMapping
    public void registerCustomer(@RequestBody CustomerRegistrationRequest customerRegistrationRequest){
        log.info("new customer registration {}", customerRegistrationRequest);
        customerService.registerCustomer(customerRegistrationRequest);
    }


    @GetMapping(value = "/check/{id}")
    public CustomerCheckResponse checkCustomerExist(@PathVariable UUID id){
        return new CustomerCheckResponse(customerService.checkCustomerExist(id));
    }
}
