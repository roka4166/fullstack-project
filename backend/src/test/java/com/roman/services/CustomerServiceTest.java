package com.roman.services;

import com.roman.DAO.CustomerDAO;
import com.roman.exceptions.DuplicateResourceException;
import com.roman.exceptions.RequestValidationException;
import com.roman.exceptions.ResourceNotFoundException;
import com.roman.models.Customer;
import com.roman.utils.CustomerRegistrationRequest;
import com.roman.utils.CustomerUpdateRequest;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    private CustomerService underTest;
    @Mock
    private CustomerDAO customerDAO;

    @BeforeEach
    void setUp() {
        underTest = new CustomerService(customerDAO);
    }

    @Test
    void itShouldGetAllCustomers() {
        //When
        underTest.getAllCustomers();
        //Then
        verify(customerDAO).selectAllCustomers();
    }

    @Test
    void itShouldGetCustomer() {
        int id = 1;
        Customer customer = new Customer(id, "hello", "email", 14, "MALE");
        when(customerDAO.selectCustomerById(id)).thenReturn(Optional.of(customer));
        //When
        Customer actual = underTest.getCustomer(id);
        //Then
        assertThat(actual).isEqualTo(customer);
    }

    @Test
    void itShouldThrowWhenCustomerIsEmpty() {
        int id = 1;
        when(customerDAO.selectCustomerById(id)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> underTest.getCustomer(id)).isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("customer with id [%s] not found".formatted(id));
    }

    @Test
    void itShouldAddCustomer() {
        // Given
        int id = 1;
        String email = "email";
        String gender = "MALE";

        CustomerRegistrationRequest request = new CustomerRegistrationRequest("alex", email, 19, gender);

        when(customerDAO.existsPersonWithEmail(email)).thenReturn(false);
        //When
        underTest.addCustomer(request);
        //Then
        ArgumentCaptor<Customer> captor = ArgumentCaptor.forClass(Customer.class);
        verify(customerDAO).insertCustomer(captor.capture());

        Customer capturedCustomer = captor.getValue();

        assertThat(capturedCustomer.getId()).isNull();
        assertThat(capturedCustomer.getName()).isEqualTo(request.name());
        assertThat(capturedCustomer.getEmail()).isEqualTo(request.email());
        assertThat(capturedCustomer.getAge()).isEqualTo(request.age());
    }

    @Test
    void itShouldThrowWhenEmailExists() {
        // Given
        int id = 1;
        String email = "email";
        String gender = "MALE";

        CustomerRegistrationRequest request = new CustomerRegistrationRequest("alex", email, 19, gender);

        when(customerDAO.existsPersonWithEmail(email)).thenReturn(true);
        //When
        //Then
        assertThatThrownBy(() -> underTest.addCustomer(request)).isInstanceOf(DuplicateResourceException.class)
                .hasMessage("email already taken");

        verify(customerDAO, never()).insertCustomer(any());
    }

    @Test
    void itShouldDeleteCustomer() {
        // Given
        int id = 1;
        when(customerDAO.existsPersonWithId(id)).thenReturn(true);
        //When
        underTest.deleteCustomer(id);
        //Then
        verify(customerDAO).deleteCustomerById(id);

    }

    @Test
    void itShouldThrowWhenCustomerIdNotExist() {
        // Given
        int id = 1;
        when(customerDAO.existsPersonWithId(id)).thenReturn(false);
        //When
        assertThatThrownBy(() -> underTest.deleteCustomer(id)).isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("customer with id [%s] not found".formatted(id));
        //Then
        verify(customerDAO, never()).deleteCustomerById(id);

    }

    @Test
    void itShouldUpdateCustomer() {
        // Given
        int id = 1;
        String gender = "MALE";
        Customer customer = new Customer(id, "hello", "email", 14, gender);
        when(customerDAO.selectCustomerById(id)).thenReturn(Optional.of(customer));
        //When
        String updatedEmail = "email2";
        CustomerUpdateRequest request = new CustomerUpdateRequest("hello2", updatedEmail, 23);
        when(customerDAO.existsPersonWithEmail(updatedEmail)).thenReturn(false);

        underTest.updateCustomer(id, request);
        //Then
        ArgumentCaptor<Customer> customerArgumentCaptor = ArgumentCaptor.forClass(Customer.class);

        verify(customerDAO).updateCustomer(customerArgumentCaptor.capture());
        Customer capturedCustomer = customerArgumentCaptor.getValue();

        assertThat(capturedCustomer.getName()).isEqualTo(request.name());
        assertThat(capturedCustomer.getEmail()).isEqualTo(request.email());
        assertThat(capturedCustomer.getAge()).isEqualTo(request.age());
    }

    @Test
    void itShouldUpdateCustomerName() {
        // Given
        int id = 1;
        String gender = "MALE";
        Customer customer = new Customer(id, "hello", "email", 14, gender);
        when(customerDAO.selectCustomerById(id)).thenReturn(Optional.of(customer));
        //When
        CustomerUpdateRequest request = new CustomerUpdateRequest("hello2", null, null);

        underTest.updateCustomer(id, request);
        //Then
        ArgumentCaptor<Customer> customerArgumentCaptor = ArgumentCaptor.forClass(Customer.class);

        verify(customerDAO).updateCustomer(customerArgumentCaptor.capture());
        Customer capturedCustomer = customerArgumentCaptor.getValue();

        assertThat(capturedCustomer.getName()).isEqualTo(request.name());
        assertThat(capturedCustomer.getEmail()).isEqualTo(customer.getEmail());
        assertThat(capturedCustomer.getAge()).isEqualTo(customer.getAge());
    }

    @Test
    void itShouldUpdateCustomerEmail() {
        // Given
        int id = 1;
        String gender = "MALE";
        Customer customer = new Customer(id, "hello", "email", 14, gender);
        when(customerDAO.selectCustomerById(id)).thenReturn(Optional.of(customer));
        //When
        String updatedEmail = "email2";
        CustomerUpdateRequest request = new CustomerUpdateRequest(null, updatedEmail, null);
        when(customerDAO.existsPersonWithEmail(updatedEmail)).thenReturn(false);

        underTest.updateCustomer(id, request);
        //Then
        ArgumentCaptor<Customer> customerArgumentCaptor = ArgumentCaptor.forClass(Customer.class);

        verify(customerDAO).updateCustomer(customerArgumentCaptor.capture());
        Customer capturedCustomer = customerArgumentCaptor.getValue();

        assertThat(capturedCustomer.getName()).isEqualTo(customer.getName());
        assertThat(capturedCustomer.getEmail()).isEqualTo(request.email());
        assertThat(capturedCustomer.getAge()).isEqualTo(customer.getAge());
    }

    @Test
    void itShouldUpdateCustomerAge() {
        // Given
        int id = 1;
        String gender = "MALE";
        Customer customer = new Customer(id, "hello", "email", 14, gender);
        when(customerDAO.selectCustomerById(id)).thenReturn(Optional.of(customer));
        //When
        CustomerUpdateRequest request = new CustomerUpdateRequest(null, null, 23);

        underTest.updateCustomer(id, request);
        //Then
        ArgumentCaptor<Customer> customerArgumentCaptor = ArgumentCaptor.forClass(Customer.class);

        verify(customerDAO).updateCustomer(customerArgumentCaptor.capture());
        Customer capturedCustomer = customerArgumentCaptor.getValue();

        assertThat(capturedCustomer.getName()).isEqualTo(customer.getName());
        assertThat(capturedCustomer.getEmail()).isEqualTo(customer.getEmail());
        assertThat(capturedCustomer.getAge()).isEqualTo(request.age());
    }

    @Test
    void itShouldThrowWhenEmailIsTaken() {
        // Given
        int id = 1;
        String gender = "MALE";
        Customer customer = new Customer(id, "hello", "email", 14, gender);
        when(customerDAO.selectCustomerById(id)).thenReturn(Optional.of(customer));
        //When
        String email = "takenEmail";
        CustomerUpdateRequest request = new CustomerUpdateRequest("hello2", email, 23);
        when(customerDAO.existsPersonWithEmail(email)).thenReturn(true);

        assertThatThrownBy(() -> underTest.updateCustomer(id, request)).isInstanceOf(DuplicateResourceException.class)
                .hasMessage("email already taken");

        verify(customerDAO, never()).updateCustomer(any());
    }

    @Test
    void itShouldThrowWhenThereIsNoChanges() {
        // Given
        int id = 1;
        String gender = "MALE";
        Customer customer = new Customer(id, "hello", "email", 14, gender);
        when(customerDAO.selectCustomerById(id)).thenReturn(Optional.of(customer));
        //When
        String email = "takenEmail";
        CustomerUpdateRequest request = new CustomerUpdateRequest("hello", "email", 14);


        assertThatThrownBy(() -> underTest.updateCustomer(id, request)).isInstanceOf(RequestValidationException.class)
                .hasMessage("no data changed");

        verify(customerDAO, never()).updateCustomer(any());
    }
}