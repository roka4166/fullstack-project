package com.roman.services;

import com.roman.DAO.CustomerDAO;
import com.roman.models.Customer;
import com.roman.repositories.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
@Service
public class CustomerJPAAccessService implements CustomerDAO {
    private CustomerRepository customerRepository;
    @Autowired
    public CustomerJPAAccessService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    public List<Customer> selectAllCustomers() {
        return customerRepository.findAll();
    }

    @Override
    public Optional<Customer> selectCustomerById(Integer customerId) {
        return customerRepository.findById(customerId);
    }
}
