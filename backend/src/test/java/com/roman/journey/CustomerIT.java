package com.roman.journey;

import com.github.javafaker.Faker;
import com.roman.DTO.CustomerDTO;
import com.roman.DTO.CustomerDTOMapper;
import com.roman.models.Customer;
import com.roman.utils.CustomerRegistrationRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
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

    private final CustomerDTOMapper mapper = new CustomerDTOMapper();
    @Autowired
    private WebTestClient webTestClient;

    @Test
    void itShouldRegisterCustomer() {
        String name = faker.name().name();
        String email = faker.internet().safeEmailAddress();
        int age = RANDOM.nextInt(1, 100);
        String gender = "MALE";
        CustomerRegistrationRequest request = new CustomerRegistrationRequest(name, email,"password", age, gender);

        String jwt = webTestClient.post()
                .uri(URI)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(request), CustomerRegistrationRequest.class)
                .exchange()
                .expectStatus()
                .isOk()
                .returnResult(Void.class)
                .getResponseHeaders()
                .get(HttpHeaders.AUTHORIZATION)
                .get(0);

        List<CustomerDTO> allCustomers = webTestClient.get()
                .uri(URI)
                .accept(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, String.format("Bearer %s", jwt))
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(new ParameterizedTypeReference<CustomerDTO>() {
                })
                .returnResult()
                .getResponseBody();


        int id = allCustomers.stream()
                .filter(c -> c.email().equals(email))
                .map(CustomerDTO::id)
                .findFirst().orElseThrow();

        CustomerDTO expectedCustomer = new CustomerDTO(
                id,
                name,
                email,
                gender,
                age,
                List.of("ROLE_USER"),
                email
        );

        assertThat(allCustomers).contains(expectedCustomer);


        webTestClient.get()
                .uri(URI + "/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, String.format("Bearer %s", jwt))
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(Customer.class)
                .value(customer -> {
                    assertThat(mapper.apply(customer)).usingRecursiveComparison()
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

        String emailForDeletion = faker.internet().safeEmailAddress();
        CustomerRegistrationRequest requestForDeletion = new CustomerRegistrationRequest(name, emailForDeletion, "password", age, gender);

        String jwt = webTestClient.post()
                .uri(URI)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(request), CustomerRegistrationRequest.class)
                .exchange()
                .expectStatus()
                .isOk()
                .returnResult(Void.class)
                .getResponseHeaders()
                .get(HttpHeaders.AUTHORIZATION)
                .get(0);

        webTestClient.post()
                .uri(URI)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(requestForDeletion), CustomerRegistrationRequest.class)
                .exchange()
                .expectStatus()
                .isOk()
                .returnResult(Void.class)
                .getResponseHeaders()
                .get(HttpHeaders.AUTHORIZATION)
                .get(0);

        List<CustomerDTO> allCustomers = webTestClient.get()
                .uri(URI)
                .accept(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, String.format("Bearer %s", jwt))
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(new ParameterizedTypeReference<CustomerDTO>() {
                })
                .returnResult()
                .getResponseBody();

        int id = allCustomers.stream()
                .filter(c -> c.email().equals(emailForDeletion))
                .map(CustomerDTO::id)
                .findFirst().orElseThrow();

        webTestClient.delete()
                .uri(URI + "/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, String.format("Bearer %s", jwt))
                .exchange()
                .expectStatus()
                .isOk();

        webTestClient.get()
                .uri(URI + "/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, String.format("Bearer %s", jwt))
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
        int updatedAge = RANDOM.nextInt(1, 100);
        String updatedGender = "FEMALE";

        CustomerRegistrationRequest request = new CustomerRegistrationRequest(name, email, "password", age, gender);
        CustomerRegistrationRequest updateRequest = new CustomerRegistrationRequest(updatedName, email, "password", updatedAge, updatedGender);

        String jwt = webTestClient.post()
                .uri(URI)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(request), CustomerRegistrationRequest.class)
                .exchange()
                .expectStatus()
                .isOk()
                .returnResult(Void.class)
                .getResponseHeaders()
                .get(HttpHeaders.AUTHORIZATION)
                .get(0);

        List<CustomerDTO> allCustomers = webTestClient.get()
                .uri(URI)
                .accept(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, String.format("Bearer %s", jwt))
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(new ParameterizedTypeReference<CustomerDTO>() {
                })
                .returnResult()
                .getResponseBody();

        int id = allCustomers.stream()
                .filter(c -> c.email().equals(email))
                .map(CustomerDTO::id)
                .findFirst().orElseThrow();

        webTestClient.put()
                .uri(URI + "/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(updateRequest), CustomerRegistrationRequest.class)
                .header(HttpHeaders.AUTHORIZATION, String.format("Bearer %s", jwt))
                .exchange()
                .expectStatus()
                .isOk();

        webTestClient.get()
                .uri(URI + "/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, String.format("Bearer %s", jwt))
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(Customer.class)
                .value(customer -> {
                    assertThat(customer.getName()).isEqualTo(updatedName);
                    assertThat(customer.getAge()).isEqualTo(updatedAge);
                });
    }
}
