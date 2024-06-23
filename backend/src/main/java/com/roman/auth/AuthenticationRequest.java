package com.roman.auth;

public record AuthenticationRequest(
        String email,
        String password
) {
}
