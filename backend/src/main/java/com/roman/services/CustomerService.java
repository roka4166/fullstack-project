package com.roman.services;

import com.roman.DAO.CustomerDAO;
import com.roman.exceptions.DuplicateResourceException;
import com.roman.exceptions.RequestValidationException;
import com.roman.exceptions.ResourceNotFoundException;
import com.roman.models.Customer;
import com.roman.utils.CustomerRegistrationRequest;
import com.roman.utils.CustomerUpdateRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerService {
    private final CustomerDAO customerDAO;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public CustomerService(@Qualifier("jdbc") CustomerDAO customerDAO, PasswordEncoder passwordEncoder) {
        this.customerDAO = customerDAO;
        this.passwordEncoder = passwordEncoder;
    }
    public List<Customer> getAllCustomers() {
        return customerDAO.selectAllCustomers();
    }

    public Customer getCustomer(Integer id) {
        return customerDAO.selectCustomerById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "customer with id [%s] not found".formatted(id)
                ));
    }

    public void addCustomer(CustomerRegistrationRequest request){
        String email = request.email();
        if(customerDAO.existsPersonWithEmail(email)){
            throw new DuplicateResourceException("email already taken");
        }
        customerDAO.insertCustomer(
                new Customer(request.name(),
                        request.email(),
                        passwordEncoder.encode(request.password()),
                        request.age(),
                        request.gender())
        );
    }

    public void deleteCustomer(Integer customerId){
        if(!customerDAO.existsPersonWithId(customerId)){
            throw new ResourceNotFoundException("customer with id [%s] not found".formatted(customerId));
        }
        customerDAO.deleteCustomerById(customerId);
    }

    public void updateCustomer(Integer customerId, CustomerUpdateRequest request){
        Customer customer = getCustomer(customerId);

        boolean changes = false;

        if(request.name() != null && !request.name().equals(customer.getName())){
            customer.setName(request.name());
            changes = true;
        }

        if(request.age() != null && !request.age().equals(customer.getAge())){
            customer.setAge(request.age());
            changes = true;
        }

        if(request.email() != null && !request.email().equals(customer.getEmail())){
            if(customerDAO.existsPersonWithEmail(request.email())){
                throw new DuplicateResourceException("email already taken");
            }
            customer.setEmail(request.email());
            changes = true;
        }

        if(!changes){
            throw new RequestValidationException("no data changed");
        }

        customerDAO.updateCustomer(customer);
    }
}
