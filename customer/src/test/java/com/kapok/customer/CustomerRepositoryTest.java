package com.kapok.customer;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest(
        properties = {
                "spring.jpa.properties.javax.persistence.validation.mode=none"
        }
)
class CustomerRepositoryTest {

    @Autowired
    private CustomerRepository underTest;

    @Test
    void itShouldFindCustomerByPhoneNumber() {
        // Given
        UUID id = UUID.randomUUID();
        Customer customer = Customer.builder()
                .id(id)
                .firstName("kapok")
                .lastName("code")
                .email("kapokoffical@gmail.com")
                .phoneNumber(131)
                .build();
        // When
        underTest.save(customer);

        // Then


        Optional<Customer> optionalCustomer = underTest.findCustomerByPhoneNumber(131);
        assertThat(optionalCustomer)
                .isPresent()
                .hasValueSatisfying(c -> {
                    assertThat(c.getId()).isEqualTo(id);
                    assertThat(c.getFirstName()).isEqualTo("kapok");
                    assertThat(c.getLastName()).isEqualTo("code");
                    assertThat(c.getPhoneNumber()).isEqualTo(131);
                });
    }

    @Test
    void itNotShouldSaveCustomerWhenNumberDoesNotExists() {
        // Given
        Integer phoneNumber = 131;

        //When
        Optional<Customer> optionalCustomer = underTest.findCustomerByPhoneNumber(phoneNumber);
        //Then
        assertThat(optionalCustomer).isNotPresent();
    }

    @Test
    void itShouldSaveCustomer() {
        // Given
        UUID id = UUID.randomUUID();
        Customer customer = Customer.builder()
                .id(id)
                .firstName("kapok")
                .lastName("code")
                .email("kapokoffical@gmail.com")
                .phoneNumber(131)
                .build();

        //When
        underTest.save(customer);

        //Then
        Optional<Customer> optionalCustomer = underTest.findById(id);
        assertThat(optionalCustomer)
                .isPresent()
                .hasValueSatisfying(c -> {
                    assertThat(c.getId()).isEqualTo(id);
                    assertThat(c.getFirstName()).isEqualTo("kapok");
                    assertThat(c.getLastName()).isEqualTo("code");
                    assertThat(c.getPhoneNumber()).isEqualTo(131);
                });
    }



    @Test
    void itShouldNotSaveCustomerWhenFirstNameIsNull() {
        // Given
        Customer customer = Customer.builder()
                .id(UUID.randomUUID())
                .firstName(null)
                .lastName("code")
                .email("kapokoffical@gmail.com")
                .phoneNumber(131)
                .build();

        // When
        // Then
        assertThatThrownBy(() ->underTest.save(customer))
                .hasMessageContaining("not-null property references a null or transient value : com.kapok.customer.Customer.firstName")
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void itShouldNotSaveCustomerWhenLastNameIsNull() {
        // Given
        Customer customer = Customer.builder()
                .id(UUID.randomUUID())
                .firstName("kapok")
                .lastName(null)
                .email("kapokoffical@gmail.com")
                .phoneNumber(131)
                .build();

        // When
        // Then
        assertThatThrownBy(() ->underTest.save(customer))
                .hasMessageContaining("not-null property references a null or transient value : com.kapok.customer.Customer.lastName")
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void itShouldNotSaveCustomerWhenEmailIsNull() {
        // Given
        Customer customer = Customer.builder()
                .id(UUID.randomUUID())
                .firstName("kapok")
                .lastName("code")
                .email(null)
                .phoneNumber(131)
                .build();

        // When
        // Then
        assertThatThrownBy(() ->underTest.save(customer))
                .hasMessageContaining("not-null property references a null or transient value : com.kapok.customer.Customer.email")
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void itShouldNotSaveCustomerWhenPhoneNumberIsNull() {
        // Given
        Customer customer = Customer.builder()
                .id(UUID.randomUUID())
                .firstName("kapok")
                .lastName("code")
                .email("kapoktest@gmail.com")
                .phoneNumber(null)
                .build();

        // When
        // Then
        assertThatThrownBy(() ->underTest.save(customer))
                .hasMessageContaining("not-null property references a null or transient value : com.kapok.customer.Customer.phoneNumber")
                .isInstanceOf(DataIntegrityViolationException.class);
    }
}