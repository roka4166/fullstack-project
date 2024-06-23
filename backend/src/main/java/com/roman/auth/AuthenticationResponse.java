package com.roman.auth;

import com.roman.DTO.CustomerDTO;

public record AuthenticationResponse(
        CustomerDTO customerDTO
) {
}
