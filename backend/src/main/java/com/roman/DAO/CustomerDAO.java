package com.roman.DAO;

import com.roman.DTO.CustomerDTO;
import com.roman.models.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface CustomerDAO {
    Page<Customer> selectAllCustomers(Pageable pageable);
    Optional<Customer> selectCustomerById(Integer customerId);

    void insertCustomer(Customer customer);
    boolean existsPersonWithEmail(String email);

    boolean existsPersonWithId(Integer customerId);

    void deleteCustomerById(Integer customerId);

    void updateCustomer(Customer customer);

    Optional<Customer> selectByEmail(String email);

    void updateCustomerProfileImageId(String profileImageId, Integer customerId);

    List<Customer> getAllCustomers();
}
