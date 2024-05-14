package com.roman.utils;

public record CustomerRegistrationRequest(
        String name,
        String email,
        Integer age
) {
}
