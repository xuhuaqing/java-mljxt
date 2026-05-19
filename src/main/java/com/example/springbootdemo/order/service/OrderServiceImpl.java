package com.example.springbootdemo.order.service;

import com.example.springbootdemo.auth.dao.UserEntity;
import com.example.springbootdemo.auth.dao.UserMapper;
import com.example.springbootdemo.device.dao.DeviceEntity;
import com.example.springbootdemo.device.dao.DeviceMapper;
import com.example.springbootdemo.device.service.DeviceUsageGuardService;
import com.example.springbootdemo.order.OrderUsagePageVO;
import com.example.springbootdemo.order.OrderUsageRecordVO;
import com.example.springbootdemo.order.PlaceOrderRequest;
import com.example.springbootdemo.order.PlaceOrderVO;
import com.example.springbootdemo.order.AdminDeviceUsageRecordVO;
import com.example.springbootdemo.order.AdminOrderRecordVO;
import com.example.springbootdemo.order.dao.OrderEntity;
import com.example.springbootdemo.order.dao.OrderMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
public class OrderServiceImpl implements OrderService {

    private final UserMapper userMapper;
    private final OrderMapper orderMapper;
    private final DeviceMapper deviceMapper;
    private final DeviceUsageGuardService deviceUsageGuardService;

    public OrderServiceImpl(
            UserMapper userMapper,
            OrderMapper orderMapper,
            DeviceMapper deviceMapper,
            DeviceUsageGuardService deviceUsageGuardService
    ) {
        this.userMapper = userMapper;
        this.orderMapper = orderMapper;
        this.deviceMapper = deviceMapper;
        this.deviceUsageGuardService = deviceUsageGuardService;
    }

    @Override
    @Transactional
    public PlaceOrderVO placeOrder(PlaceOrderRequest request) {
        String phone = request.getPhone().trim();
        UserEntity user = userMapper.findByPhoneAndRole(phone, 1);
        boolean created = false;
        String initialPassword = null;

        String userName = request.getName().trim();

        if (user == null) {
            initialPassword = phone.substring(phone.length() - 4);
            UserEntity newUser = new UserEntity();
            newUser.setPhone(phone);
            newUser.setName(userName);
            newUser.setPassword(initialPassword);
            newUser.setRole(1);
            newUser.setStatus(1);
            newUser.setRemainingUseCount(0);
            userMapper.insert(newUser);
            user = newUser;
            created = true;
        } else if (!Objects.equals(user.getStatus(), 1)) {
            throw new IllegalArgumentException("账号已停用");
        } else if (!userName.equals(user.getName())) {
            user.setName(userName);
            userMapper.updateById(user);
        }

        Long deviceId = request.getDeviceId();
        if (deviceId != null) {
            DeviceEntity device = deviceMapper.findById(deviceId);
            if (device == null) {
                throw new IllegalArgumentException("设备不存在");
            }
            if (device.getStatus() != null && device.getStatus() == 0) {
                throw new IllegalArgumentException("设备已停用");
            }
            deviceUsageGuardService.lockDeviceAndAssertAvailable(deviceId, user.getId());
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
                user.getName(),
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
    public OrderUsagePageVO queryOrderRecords(String phone, Long userId, Long merchantId, Long deviceId, Integer pageNo, Integer pageSize) {
        String normalizedPhone = phone == null ? null : phone.trim();
        if (normalizedPhone != null && normalizedPhone.isEmpty()) {
            normalizedPhone = null;
        }
        if (normalizedPhone != null && !normalizedPhone.matches("\\d{11}")) {
            throw new IllegalArgumentException("手机号必须是11位数字");
        }
        if (userId != null && userId <= 0) {
            throw new IllegalArgumentException("userId必须大于0");
        }
        if (merchantId != null && merchantId <= 0) {
            throw new IllegalArgumentException("merchantId必须大于0");
        }
        if (deviceId != null && deviceId <= 0) {
            throw new IllegalArgumentException("deviceId必须大于0");
        }

        int finalPageNo = (pageNo == null || pageNo < 1) ? 1 : pageNo;
        int finalPageSize = (pageSize == null || pageSize < 1) ? 10 : Math.min(pageSize, 100);
        int offset = (finalPageNo - 1) * finalPageSize;

        long total = orderMapper.countOrderRecords(normalizedPhone, userId, merchantId, deviceId);
        List<OrderUsageRecordVO> records = orderMapper.queryOrderRecords(normalizedPhone, userId, merchantId, deviceId, offset, finalPageSize)
                .stream()
                .map(order -> new OrderUsageRecordVO(
                        order.getId(),
                        order.getUserId(),
                        order.getUserName(),
                        order.getUserPhone(),
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

    @Override
    public OrderUsagePageVO queryUsageRecords(String phone, Long userId, Long deviceId, Integer pageNo, Integer pageSize) {
        String normalizedPhone = phone == null ? null : phone.trim();
        if (normalizedPhone != null && normalizedPhone.isEmpty()) {
            normalizedPhone = null;
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
                        order.getUserName(),
                        order.getUserPhone(),
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

    @Override
    public List<AdminDeviceUsageRecordVO> queryAdminDeviceUsageRecords(Long merchantId, Long deviceId, int pageNo, int pageSize) {
        int offset = (pageNo - 1) * pageSize;
        return orderMapper.queryAdminDeviceUsageRecords(merchantId, deviceId, offset, pageSize);
    }

    @Override
    public long countAdminDeviceUsageRecords(Long merchantId, Long deviceId) {
        return orderMapper.countAdminDeviceUsageRecords(merchantId, deviceId);
    }

    @Override
    public List<AdminDeviceUsageRecordVO> queryAdminDeviceUsageRecordsForExport(Long merchantId, Long deviceId) {
        return orderMapper.queryAdminDeviceUsageRecordsForExport(merchantId, deviceId);
    }

    @Override
    public List<AdminOrderRecordVO> queryAdminOrderRecords(Long merchantId, Long deviceId, String phone, int pageNo, int pageSize) {
        String normalizedPhone = phone == null ? null : phone.trim();
        if (normalizedPhone != null && normalizedPhone.isEmpty()) {
            normalizedPhone = null;
        }
        if (normalizedPhone != null && !normalizedPhone.matches("\\d{11}")) {
            throw new IllegalArgumentException("手机号必须是11位数字");
        }
        int offset = (pageNo - 1) * pageSize;
        return orderMapper.queryAdminOrderRecords(merchantId, deviceId, normalizedPhone, offset, pageSize);
    }

    @Override
    public long countAdminOrderRecords(Long merchantId, Long deviceId, String phone) {
        String normalizedPhone = phone == null ? null : phone.trim();
        if (normalizedPhone != null && normalizedPhone.isEmpty()) {
            normalizedPhone = null;
        }
        if (normalizedPhone != null && !normalizedPhone.matches("\\d{11}")) {
            throw new IllegalArgumentException("手机号必须是11位数字");
        }
        return orderMapper.countAdminOrderRecords(merchantId, deviceId, normalizedPhone);
    }
}
