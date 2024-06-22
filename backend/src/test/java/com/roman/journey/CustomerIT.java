package com.roman.journey;

import com.github.javafaker.Faker;
import com.roman.models.Customer;
import com.roman.utils.CustomerRegistrationRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CustomerIT {

    private static final Random RANDOM = new Random();
    private static final String URI = "/api/v1/customers";

    private final Faker faker = new Faker();
    @Autowired
    private WebTestClient webTestClient;

    @Test
    void itShouldRegisterCustomer() {
        String name = faker.name().name();
        String email = faker.internet().safeEmailAddress();
        int age = RANDOM.nextInt(1, 100);
        String gender = "MALE";
        CustomerRegistrationRequest request = new CustomerRegistrationRequest(name, email,"password", age, gender);

        webTestClient.post()
                .uri(URI)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(request), CustomerRegistrationRequest.class)
                .exchange()
                .expectStatus()
                .isOk();

        List<Customer> allCustomers = webTestClient.get()
                .uri(URI)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(new ParameterizedTypeReference<Customer>() {
                })
                .returnResult()
                .getResponseBody();

        Customer expectedCustomer = new Customer(name, email, "password", age, gender);

        assertThat(allCustomers).usingRecursiveFieldByFieldElementComparatorIgnoringFields("id")
                .contains(expectedCustomer);

        int id = allCustomers.stream()
                .filter(c -> c.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst().orElseThrow();

        expectedCustomer.setId(id);

        webTestClient.get()
                .uri(URI + "/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(Customer.class)
                .value(customer -> {
                    assertThat(customer).usingRecursiveComparison()
                            .isEqualTo(expectedCustomer);
                });
    }
    @Test
    void canDeleteCustomer(){
        String name = faker.name().name();
        String email = faker.internet().safeEmailAddress();
        int age = RANDOM.nextInt(1, 100);
        String gender = "MALE";
        CustomerRegistrationRequest request = new CustomerRegistrationRequest(name, email, "password", age, gender);

        webTestClient.post()
                .uri(URI)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(request), CustomerRegistrationRequest.class)
                .exchange()
                .expectStatus()
                .isOk();

        List<Customer> allCustomers = webTestClient.get()
                .uri(URI)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(new ParameterizedTypeReference<Customer>() {
                })
                .returnResult()
                .getResponseBody();

        int id = allCustomers.stream()
                .filter(c -> c.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst().orElseThrow();

        webTestClient.delete()
                .uri(URI + "/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk();

        webTestClient.get()
                .uri(URI + "/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isNotFound();
    }
    @Test
    void canUpdateCustomer(){
        String name = faker.name().name();
        String email = faker.internet().safeEmailAddress();
        int age = RANDOM.nextInt(1, 100);
        String gender = "MALE";

        String updatedName = faker.name().name();
        String updatedEmail = faker.internet().safeEmailAddress();
        int updatedAge = RANDOM.nextInt(1, 100);
        String updatedGender = "FEMALE";

        CustomerRegistrationRequest request = new CustomerRegistrationRequest(name, email, "password", age, gender);
        CustomerRegistrationRequest updateRequest = new CustomerRegistrationRequest(updatedName, updatedEmail, "password", updatedAge, updatedGender);

        webTestClient.post()
                .uri(URI)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(request), CustomerRegistrationRequest.class)
                .exchange()
                .expectStatus()
                .isOk();

        List<Customer> allCustomers = webTestClient.get()
                .uri(URI)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(new ParameterizedTypeReference<Customer>() {
                })
                .returnResult()
                .getResponseBody();

        int id = allCustomers.stream()
                .filter(c -> c.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst().orElseThrow();

        webTestClient.put()
                .uri(URI + "/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(updateRequest), CustomerRegistrationRequest.class)
                .exchange()
                .expectStatus()
                .isOk();

        webTestClient.get()
                .uri(URI + "/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(Customer.class)
                .value(customer -> {
                    assertThat(customer.getName()).isEqualTo(updatedName);
                    assertThat(customer.getEmail()).isEqualTo(updatedEmail);
                    assertThat(customer.getAge()).isEqualTo(updatedAge);
                });
    }
}
