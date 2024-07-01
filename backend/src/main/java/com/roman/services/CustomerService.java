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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CustomerService {

    private static final String PATH = "C:\\Users\\russi\\Desktop\\fullstack-project\\backend\\src\\main\\resources\\static\\fakeS3";
    private final CustomerDAO customerDAO;
    private final PasswordEncoder passwordEncoder;
    private final CustomerDTOMapper customerDTOMapper;

    @Autowired
    public CustomerService(@Qualifier("jpa") CustomerDAO customerDAO, PasswordEncoder passwordEncoder, CustomerDTOMapper customerDTOMapper) {
        this.customerDAO = customerDAO;
        this.passwordEncoder = passwordEncoder;
        this.customerDTOMapper = customerDTOMapper;
    }
    public Page<CustomerDTO> getCustomers(Pageable pageable) {
        Page<Customer> customersPage = customerDAO.selectAllCustomers(pageable);
        return customersPage.map(customerDTOMapper);
    }
    public List<CustomerDTO> getAllCustomers() {
        return customerDAO.getAllCustomers().stream().map(customerDTOMapper).collect(Collectors.toList());
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
                        request.gender(),
                        request.pictureId())
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
        String pictureID = UUID.randomUUID().toString();
        try {
            File directory = new File(PATH);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            String originalFileName = StringUtils.cleanPath(file.getOriginalFilename());
            String extension = originalFileName.substring(originalFileName.lastIndexOf("."));

            String filePath = PATH + File.separator + pictureID + extension;
            File destinationFile = new File(filePath);

            file.transferTo(destinationFile);

        } catch (Exception e) {
            throw new RuntimeException("failed to upload profile image", e);
        }
        customerDAO.updateCustomerProfileImageId(pictureID, customerId);
    }

    public byte[] getCustomerProfileImage(Integer customerId) throws IOException {
        Customer customer = customerDAO.selectCustomerById(customerId).orElse(null);
        if (customer == null) {
            throw new ResourceNotFoundException(
                    "customer with id [%s] profile image not found".formatted(customerId));
        }
        String pictureId = customer.getPicture_id();
        String imagePath = PATH + File.separator + pictureId + ".jpg";

        Path path = Paths.get(imagePath);

        return Files.readAllBytes(path);
    }
    private void checkIfCustomerExistsOrThrow(Integer customerId) {
        if (!customerDAO.existsPersonWithId(customerId)) {
            throw new ResourceNotFoundException(
                    "customer with id [%s] not found".formatted(customerId)
            );
        }
    }
}
