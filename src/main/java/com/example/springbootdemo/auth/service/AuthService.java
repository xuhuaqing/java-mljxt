package com.example.springbootdemo.auth.service;

import com.example.springbootdemo.auth.LoginRequest;
import com.example.springbootdemo.auth.LoginUserVO;

public interface AuthService {
    LoginUserVO login(LoginRequest request);
}
