package com.roman.services;

import com.roman.DAO.CustomerDAO;
import com.roman.DTO.CustomerDTO;
import com.roman.DTO.CustomerDTOMapper;
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
import java.util.stream.Collectors;

@Service
public class CustomerService {
    private final CustomerDAO customerDAO;
    private final PasswordEncoder passwordEncoder;
    private final CustomerDTOMapper customerDTOMapper;

    @Autowired
    public CustomerService(@Qualifier("jpa") CustomerDAO customerDAO, PasswordEncoder passwordEncoder, CustomerDTOMapper customerDTOMapper) {
        this.customerDAO = customerDAO;
        this.passwordEncoder = passwordEncoder;
        this.customerDTOMapper = customerDTOMapper;
    }
    public List<CustomerDTO> getAllCustomers() {
        return customerDAO.selectAllCustomers().stream().map(customerDTOMapper).collect(Collectors.toList());
    }

    public CustomerDTO getCustomer(Integer id) {
        return customerDAO.selectCustomerById(id).map(customerDTOMapper)
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
                        request.gender(), )
        );
    }

    public void deleteCustomer(Integer customerId){
        if(!customerDAO.existsPersonWithId(customerId)){
            throw new ResourceNotFoundException("customer with id [%s] not found".formatted(customerId));
        }
        customerDAO.deleteCustomerById(customerId);
    }

    public void updateCustomer(Integer customerId, CustomerUpdateRequest request){
        Customer customer = customerDAO.selectCustomerById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException(
                "customer with id [%s] not found".formatted(customerId)
        ));

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

    public void uploadCustomerProfileImage(Integer customerId,
                                           MultipartFile file) {
        checkIfCustomerExistsOrThrow(customerId);
        String profileImageId = UUID.randomUUID().toString();
        try {
            s3Service.putObject(
                    s3Buckets.getCustomer(),
                    "profile-images/%s/%s".formatted(customerId, profileImageId),
                    file.getBytes()
            );
        } catch (IOException e) {
            throw new RuntimeException("failed to upload profile image", e);
        }
        customerDao.updateCustomerProfileImageId(profileImageId, customerId);
    }

    public byte[] getCustomerProfileImage(Integer customerId) {
        var customer = customerDao.selectCustomerById(customerId)
                .map(customerDTOMapper)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "customer with id [%s] not found".formatted(customerId)
                ));

        if (StringUtils.isBlank(customer.profileImageId())) {
            throw new ResourceNotFoundException(
                    "customer with id [%s] profile image not found".formatted(customerId));
        }

        byte[] profileImage = s3Service.getObject(
                s3Buckets.getCustomer(),
                "profile-images/%s/%s".formatted(customerId, customer.profileImageId())
        );
        return profileImage;
    }
}
