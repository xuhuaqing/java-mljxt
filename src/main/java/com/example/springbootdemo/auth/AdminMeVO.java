package com.example.springbootdemo.auth;

public record AdminMeVO(
        Long id,
        String phone,
        Integer role,
        String roleName,
        Integer remainingUseCount
) {
}
