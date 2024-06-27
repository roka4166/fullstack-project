package com.roman.models;

import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "customer", uniqueConstraints = {@UniqueConstraint(name = "customer_email_unique", columnNames = "email")})
public class Customer implements UserDetails {
    @Id
    @SequenceGenerator(
            name = "customer_id_seq",
            sequenceName = "customer_id_seq",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "customer_id_seq"
    )
    private Integer id;
    @Column(
            nullable = false
    )
    private String name;
    @Column(
            nullable = false
    )
    private String email;
    @Column(
            nullable = false
    )
    private Integer age;
    @Column(
            nullable = false
    )
    private String gender;
    @Column(
            nullable = false
    )
    private String password;
    @Column(
            nullable = false
    )
    private String pictureId;

    public Customer(){}

    public Customer(Integer id, String name, String email, String password, Integer age,
                    String gender, String pictureID) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.age = age;
        this.gender = gender;
        this.password = password;
    }

    public Customer(String name, String email, String password, Integer age,
                    String gender, String pictureID) {
        this.name = name;
        this.email = email;
        this.age = age;
        this.gender = gender;
        this.password = password;

    }


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public String getPictureId() {
        return pictureId;
    }

    public void setPictureId(String pictureID) {
        this.pictureId = pictureID;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Customer customer)) return false;
        return Objects.equals(id, customer.id) && Objects.equals(name, customer.name) && Objects.equals(email, customer.email) && Objects.equals(age, customer.age) && Objects.equals(gender, customer.gender) && Objects.equals(password, customer.password) && Objects.equals(pictureId, customer.pictureId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, email, age, gender, password, pictureId);
    }
}
