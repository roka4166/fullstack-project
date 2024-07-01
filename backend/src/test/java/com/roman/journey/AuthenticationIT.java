package com.roman.journey;

import com.github.javafaker.Faker;
import com.roman.DTO.CustomerDTOMapper;
import com.roman.auth.AuthenticationRequest;
import com.roman.utils.CustomerRegistrationRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.Random;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AuthenticationIT {

    private static final Random RANDOM = new Random();
    private static final String LOGIN_URI = "/api/v1/auth";
    private static final String REGISTER_CUSTOMER_URI = "/api/v1/customers";

    private final Faker faker = new Faker();

    private final CustomerDTOMapper mapper = new CustomerDTOMapper();
    @Autowired
    private WebTestClient webTestClient;

    @Test
    void itShouldLogin() {
        String name = faker.name().name();
        String email = faker.internet().safeEmailAddress();
        int age = RANDOM.nextInt(1, 100);
        String gender = "MALE";
        String password = "password";
        CustomerRegistrationRequest request = new CustomerRegistrationRequest(name, email, password, age, gender, "foo");

        AuthenticationRequest authenticationRequest = new AuthenticationRequest(email, password);

        String jwt = webTestClient.post()
                .uri(REGISTER_CUSTOMER_URI)
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
                .uri(LOGIN_URI+"/login")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(authenticationRequest), AuthenticationRequest.class)
                .header(HttpHeaders.AUTHORIZATION, String.format("Bearer %s", jwt))
                .exchange()
                .expectStatus()
                .isOk();


    }
}
