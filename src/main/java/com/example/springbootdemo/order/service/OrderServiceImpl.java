package com.example.springbootdemo.order.service;

import com.example.springbootdemo.auth.dao.UserEntity;
import com.example.springbootdemo.auth.dao.UserMapper;
import com.example.springbootdemo.order.OrderUsagePageVO;
import com.example.springbootdemo.order.OrderUsageRecordVO;
import com.example.springbootdemo.order.PlaceOrderRequest;
import com.example.springbootdemo.order.PlaceOrderVO;
import com.example.springbootdemo.order.dao.OrderEntity;
import com.example.springbootdemo.order.dao.OrderMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    private final UserMapper userMapper;
    private final OrderMapper orderMapper;

    public OrderServiceImpl(UserMapper userMapper, OrderMapper orderMapper) {
        this.userMapper = userMapper;
        this.orderMapper = orderMapper;
    }

    @Override
    @Transactional
    public PlaceOrderVO placeOrder(PlaceOrderRequest request) {
        String phone = request.getPhone().trim();
        UserEntity user = userMapper.findByPhone(phone);
        boolean created = false;
        String initialPassword = null;

        if (user == null) {
            initialPassword = phone.substring(phone.length() - 4);
            UserEntity newUser = new UserEntity();
            newUser.setPhone(phone);
            newUser.setName("用户" + initialPassword);
            newUser.setPassword(initialPassword);
            newUser.setRole(1);
            userMapper.insert(newUser);
            user = newUser;
            created = true;
        }

        UserEntity merchant = userMapper.findMerchantById(request.getMerchantId());
        if (merchant == null) {
            throw new IllegalArgumentException("商家不存在");
        }
        if (merchant.getRemainingUseCount() == null || merchant.getRemainingUseCount() <= 0) {
            throw new IllegalArgumentException("商家剩余使用次数不足，用户不能使用设备");
        }

        int affected = userMapper.decrementMerchantRemainingUseCount(request.getMerchantId());
        if (affected == 0) {
            throw new IllegalArgumentException("商家剩余使用次数不足，用户不能使用设备");
        }

        OrderEntity order = new OrderEntity();
        order.setUserId(user.getId());
        order.setMerchantId(request.getMerchantId());
        order.setDeviceId(request.getDeviceId());
        order.setGender(request.getGender());
        order.setAge(request.getAge());
        order.setHeight(request.getHeight());
        order.setWeight(request.getWeight());
        order.setSportPerformance(request.getSportPerformance());
        order.setProjectName(request.getProjectName().trim());
        order.setProjectDuration(request.getProjectDuration());
        order.setUsageCount(request.getUsageCount());
        orderMapper.insert(order);

        return new PlaceOrderVO(
                order.getId(),
                user.getId(),
                user.getPhone(),
                order.getMerchantId(),
                order.getProjectName(),
                order.getProjectDuration(),
                order.getUsageCount(),
                created,
                initialPassword
        );
    }

    @Override
    public OrderUsagePageVO queryUsageRecords(String phone, Long userId, Long deviceId, Integer pageNo, Integer pageSize) {
        String normalizedPhone = phone == null ? null : phone.trim();
        if (normalizedPhone != null && normalizedPhone.isEmpty()) {
            normalizedPhone = null;
        }
        if (normalizedPhone == null && userId == null && deviceId == null) {
            throw new IllegalArgumentException("phone、userId和deviceId不能同时为空");
        }
        if (normalizedPhone != null && !normalizedPhone.matches("\\d{11}")) {
            throw new IllegalArgumentException("手机号必须是11位数字");
        }
        if (userId != null && userId <= 0) {
            throw new IllegalArgumentException("userId必须大于0");
        }
        if (deviceId != null && deviceId <= 0) {
            throw new IllegalArgumentException("deviceId必须大于0");
        }

        int finalPageNo = (pageNo == null || pageNo < 1) ? 1 : pageNo;
        int finalPageSize = (pageSize == null || pageSize < 1) ? 10 : Math.min(pageSize, 100);
        int offset = (finalPageNo - 1) * finalPageSize;

        long total = orderMapper.countUsageRecords(normalizedPhone, userId, deviceId);
        List<OrderUsageRecordVO> records = orderMapper.queryUsageRecords(normalizedPhone, userId, deviceId, offset, finalPageSize)
                .stream()
                .map(order -> new OrderUsageRecordVO(
                        order.getId(),
                        order.getUserId(),
                        order.getMerchantId(),
                        order.getDeviceName(),
                        order.getProjectName(),
                        order.getProjectDuration(),
                        order.getUsageCount(),
                        order.getSportPerformance(),
                        order.getCreatedAt()
                ))
                .toList();

        return new OrderUsagePageVO(total, finalPageNo, finalPageSize, records);
    }
}
