package com.example.springbootdemo.auth.service;

import com.example.springbootdemo.auth.LoginRequest;
import com.example.springbootdemo.auth.LoginUserVO;
import com.example.springbootdemo.auth.AdminMeVO;
import com.example.springbootdemo.auth.AdminUserVO;
import com.example.springbootdemo.auth.AdminUserUpsertRequest;

import java.util.List;

public interface AuthService {
    LoginUserVO login(LoginRequest request);

    AdminMeVO me(Long userId);

    List<AdminUserVO> listUsers(Integer role, String keyword, int pageNo, int pageSize);

    long countUsers(Integer role, String keyword);

    AdminUserVO createUser(AdminUserUpsertRequest request);

    AdminUserVO updateUser(Long id, AdminUserUpsertRequest request);

    void deleteUser(Long id);

    void enableUser(Long id);
}
