package com.example.springbootdemo.auth;

public record AdminUserVO(
        Long id,
        String name,
        String phone,
        String password,
        Integer role,
        String roleName,
        Integer status,
        Integer remainingUseCount
) {
}
