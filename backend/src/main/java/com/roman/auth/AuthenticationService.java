package com.roman.auth;

import com.roman.DTO.CustomerDTO;
import com.roman.DTO.CustomerDTOMapper;
import com.roman.jwt.JWTUtil;
import com.roman.models.Customer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.net.http.HttpResponse;

@Service
public class AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final CustomerDTOMapper mapper;

    private JWTUtil jwtUtil;
    public AuthenticationService(AuthenticationManager authenticationManager, CustomerDTOMapper mapper, JWTUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.mapper = mapper;
        this.jwtUtil = jwtUtil;
    }
    public AuthenticationResponse login(AuthenticationRequest request){
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                request.email(), request.password()
        ));
        Customer customer = (Customer) authentication.getPrincipal();
        CustomerDTO customerDTO = mapper.apply(customer);
        String jwt = jwtUtil.issueToken(customerDTO.email(), "ROLE_USER");
        return ResponseEntity.ok().header(HttpHeaders.AUTHORIZATION, jwt);
    }
}
