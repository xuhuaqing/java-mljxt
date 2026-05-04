package com.example.springbootdemo.security;

public record TokenPayload(
        Long userId,
        Integer role,
        long exp
) {
}
