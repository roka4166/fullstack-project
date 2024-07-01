package com.roman.services;

import com.roman.DAO.CustomerDAO;
import com.roman.models.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository("jdbc")
public class CustomerJDBCDataAccessService implements CustomerDAO {

    private final JdbcTemplate jdbcTemplate;
    private final CustomerRowMapper customerRowMapper;

    public CustomerJDBCDataAccessService(JdbcTemplate jdbcTemplate, CustomerRowMapper customerRowMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.customerRowMapper = customerRowMapper;
    }

    @Override
    public Page<Customer> selectAllCustomers(Pageable pageable) {
        int pageNumber = pageable.getPageNumber();

        int offset = (pageNumber - 1);
        String sql = "SELECT * FROM customer LIMIT 5 OFFSET ?";
        List<Customer> customers = jdbcTemplate.query(sql, customerRowMapper, offset);

        String countQuery = "SELECT COUNT(*) FROM customer";
        int totalCount = Integer.parseInt(countQuery);

        return new PageImpl<>(customers, pageable, totalCount);
    }


    @Override
    public Optional<Customer> selectCustomerById(Integer customerId) {
        String sql = "SELECT * FROM customer WHERE id = ?";
        return jdbcTemplate.query(sql, customerRowMapper, customerId).stream().findFirst();
    }

    @Override
    public void insertCustomer(Customer customer) {
        String sql = "INSERT INTO customer(name, email, password, age, gender, picture_id) VALUES (?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql, customer.getName(),
                customer.getEmail(), customer.getPassword(),
                customer.getAge(), customer.getGender(),
                customer.getPicture_id());
    }

    @Override
    public boolean existsPersonWithEmail(String email) {
        String sql = "SELECT count(*) FROM customer WHERE email = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, email);
        return count != null && count > 0;
    }

    @Override
    public boolean existsPersonWithId(Integer customerId) {
        String sql = "SELECT count(*) FROM customer WHERE id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, customerId);
        return count != null && count > 0;
    }

    @Override
    public void deleteCustomerById(Integer customerId) {
        String sql = "DELETE FROM customer WHERE id = ?";
        jdbcTemplate.update(sql, customerId);
    }

    @Override
    public void updateCustomer(Customer customer) {
        if(customer.getName() != null){
            String sql = "UPDATE customer SET name = ? WHERE id = ?";
            jdbcTemplate.update(sql, customer.getName(), customer.getId());
        }
        if(customer.getEmail() != null){
            String sql = "UPDATE customer SET email = ? WHERE id = ?";
            jdbcTemplate.update(sql, customer.getEmail(), customer.getId());
        }
        if(customer.getAge() != null){
            String sql = "UPDATE customer SET age = ? WHERE id = ?";
            jdbcTemplate.update(sql, customer.getAge(), customer.getId());
        }
        if(customer.getGender() != null){
            String sql = "UPDATE customer SET gender = ? WHERE id = ?";
            jdbcTemplate.update(sql, customer.getGender(), customer.getId());
        }
    }

    @Override
    public Optional<Customer> selectByEmail(String email) {
        String sql = "SELECT * FROM customer WHERE email = ?";
        return jdbcTemplate.query(sql, customerRowMapper, email).stream().findFirst();
    }

    @Override
    public void updateCustomerProfileImageId(String pictureID, Integer customerId) {
        String sql = "UPDATE customer SET picture_id = ? WHERE id = ?";
        jdbcTemplate.update(sql, pictureID, customerId);
    }

    @Override
    public List<Customer> getAllCustomers() {
        String sql = "SELECT * FROM customer";
        return jdbcTemplate.query(sql, customerRowMapper);
    }
}
