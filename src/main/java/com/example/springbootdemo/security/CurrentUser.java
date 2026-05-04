package com.example.springbootdemo.security;

public record CurrentUser(
        Long userId,
        Integer role
) {
}
