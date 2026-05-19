package com.example.springbootdemo.auth.service;

import com.example.springbootdemo.auth.MerchantOptionVO;
import com.example.springbootdemo.auth.dao.UserEntity;
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
    public List<MerchantOptionVO> getMerchantOptions(String keyword, Long userId, String phone) {
        String normalized = keyword == null ? null : keyword.trim();
        if (normalized != null && normalized.isEmpty()) {
            normalized = null;
        }

        Long orderUserId = resolveOrderUserId(userId, phone);
        List<UserEntity> rows;
        if (orderUserId != null) {
            rows = userMapper.searchMerchantsByUserOrders(orderUserId, normalized);
        } else {
            rows = userMapper.searchMerchants(normalized);
        }
        return rows.stream()
                .map(u -> new MerchantOptionVO(u.getId(), u.getName()))
                .toList();
    }

    private Long resolveOrderUserId(Long userId, String phone) {
        if (userId != null && userId > 0) {
            return userId;
        }
        if (phone == null || phone.isBlank()) {
            return null;
        }
        String normalizedPhone = phone.trim();
        if (!normalizedPhone.matches("\\d{11}")) {
            throw new IllegalArgumentException("手机号必须是11位数字");
        }
        UserEntity user = userMapper.findByPhoneAndRole(normalizedPhone, 1);
        if (user == null) {
            return null;
        }
        return user.getId();
    }
}
