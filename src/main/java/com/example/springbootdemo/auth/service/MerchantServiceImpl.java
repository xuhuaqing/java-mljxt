package com.example.springbootdemo.auth.service;

import com.example.springbootdemo.auth.MerchantOptionVO;
import com.example.springbootdemo.auth.dao.UserMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MerchantServiceImpl implements MerchantService {

    private final UserMapper userMapper;

    public MerchantServiceImpl(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    public List<MerchantOptionVO> getMerchantOptions(String keyword) {
        String normalized = keyword == null ? null : keyword.trim();
        return userMapper.searchMerchants(normalized).stream()
                .map(u -> new MerchantOptionVO(u.getId(), u.getName()))
                .toList();
    }
}
