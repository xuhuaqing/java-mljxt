package com.example.springbootdemo.auth;

public record LoginUserVO(
        Long id,
        String phone,
        Integer role,
        String roleName,
        Integer remainingUseCount,
        String token
) {
}
