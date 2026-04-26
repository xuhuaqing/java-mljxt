package com.example.springbootdemo.auth.controller;

import com.example.springbootdemo.auth.LoginRequest;
import com.example.springbootdemo.auth.LoginUserVO;
import com.example.springbootdemo.auth.service.AuthService;
import com.example.springbootdemo.common.ApiResponse;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ApiResponse<LoginUserVO> login(@Valid @RequestBody LoginRequest request) {
        log.info("调用接口 /api/auth/login, phone={}, role={}", request.getPhone(), request.getRole());
        LoginUserVO user = authService.login(request);
        log.info("登录成功 /api/auth/login, userId={}, role={}", user.id(), user.role());
        return ApiResponse.success(user);
    }
}
