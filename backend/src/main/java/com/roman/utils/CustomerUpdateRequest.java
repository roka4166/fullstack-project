package com.roman.utils;

public record CustomerUpdateRequest (
    String name,
    String email,
    Integer age
) {
}
