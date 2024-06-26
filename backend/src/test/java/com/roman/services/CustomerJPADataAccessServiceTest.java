package com.roman.services;

import com.roman.models.Customer;
import com.roman.repositories.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;

import static org.mockito.Mockito.verify;
@ExtendWith(MockitoExtension.class)
class CustomerJPADataAccessServiceTest {

    private CustomerJPADataAccessService underTest;
    @Mock
    private CustomerRepository customerRepository;

    @BeforeEach
    void setUp() {
        underTest = new CustomerJPADataAccessService(customerRepository);
    }
    @Test
    void itShouldSelectAllCustomers() {
        //When
        underTest.selectAllCustomers(Pageable.ofSize(5).withPage(1));
        //Then
        verify(customerRepository).findAll(Pageable.ofSize(5).withPage(1));

    }

    @Test
    void itShouldSelectCustomerById() {
        // Given
        int id = 1;
        //When
        underTest.selectCustomerById(id);
        //Then
        verify(customerRepository).findById(id);

    }

    @Test
    void itShouldInsertCustomer() {
        // Given
        Customer customer = new Customer(1, "drf", "hello", "password", 2, "MALE", "foo" );
        //When
        underTest.insertCustomer(customer);
        //Then
        verify(customerRepository).save(customer);

    }

    @Test
    void itShouldUpdateCustomer() {
        // Given
        Customer customer = new Customer(1, "drf", "hello", "password", 2, "MALE", "foo" );
        //When
        underTest.updateCustomer(customer);
        //Then
        verify(customerRepository).save(customer);

    }

    @Test
    void itShouldExistsPersonWithId() {
        // Given
        int id = 1;
        //When
        underTest.existsPersonWithId(id);
        //Then
        verify(customerRepository).existsById(id);

    }

    @Test
    void itShouldDeleteCustomerById() {
        // Given
        int id = 1;
        //When
        underTest.deleteCustomerById(id);
        //Then
        verify(customerRepository).deleteById(id);

    }

    @Test
    void itShouldExistsPersonWithEmail() {
        // Given
        String email = "email";
        //When
        underTest.existsPersonWithEmail(email);
        //Then
        verify(customerRepository).existsCustomerByEmail(email);

    }
}