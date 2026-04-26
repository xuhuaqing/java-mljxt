package com.example.springbootdemo.auth.service;

import com.example.springbootdemo.auth.LoginRequest;
import com.example.springbootdemo.auth.LoginUserVO;
import com.example.springbootdemo.auth.UserRole;
import com.example.springbootdemo.auth.dao.UserEntity;
import com.example.springbootdemo.auth.dao.UserMapper;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserMapper userMapper;

    public AuthServiceImpl(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    public LoginUserVO login(LoginRequest request) {
        UserRole.labelOf(request.getRole());
        UserEntity user = userMapper.findByPhoneAndRole(request.getPhone(), request.getRole());
        if (user == null) {
            throw new IllegalArgumentException("手机号或身份不存在");
        }
        if (!Objects.equals(user.getPassword(), request.getPassword())) {
            throw new IllegalArgumentException("密码错误");
        }
        return new LoginUserVO(
                user.getId(),
                user.getPhone(),
                user.getRole(),
                UserRole.labelOf(user.getRole()),
                user.getRole() != null && user.getRole() == UserRole.MERCHANT.getCode()
                        ? user.getRemainingUseCount()
                        : null
        );
    }
}
