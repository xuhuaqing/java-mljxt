package com.example.springbootdemo.device.service;

import com.example.springbootdemo.device.dao.DeviceMapper;
import com.example.springbootdemo.order.dao.ActiveDeviceUsageRow;
import com.example.springbootdemo.order.dao.OrderMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
public class DeviceUsageGuardService {

    private final OrderMapper orderMapper;
    private final DeviceMapper deviceMapper;

    public DeviceUsageGuardService(OrderMapper orderMapper, DeviceMapper deviceMapper) {
        this.orderMapper = orderMapper;
        this.deviceMapper = deviceMapper;
    }

    /**
     * 在事务内调用：锁定设备行后检查是否仍有进行中的使用（已发 MQTT 且未超过项目时长）。
     */
    public void lockDeviceAndAssertAvailable(Long deviceId, Long requestingUserId) {
        if (deviceId == null) {
            return;
        }
        Long locked = deviceMapper.selectDeviceIdForUpdate(deviceId);
        if (locked == null) {
            throw new IllegalArgumentException("设备不存在");
        }
        assertNotInUse(deviceId, requestingUserId);
    }

    public void assertNotInUse(Long deviceId, Long requestingUserId) {
        ActiveDeviceUsageRow active = orderMapper.findActiveUsageByDeviceId(deviceId);
        if (active == null) {
            return;
        }
        String machineLabel = formatMachineLabel(active.getMachineNo());
        if (requestingUserId != null && Objects.equals(active.getUserId(), requestingUserId)) {
            throw new IllegalArgumentException("设备（机号" + machineLabel + "）正在使用中，请等待当前项目结束后再操作");
        }
        String occupant = active.getUserName();
        if (occupant == null || occupant.isBlank()) {
            occupant = maskPhone(active.getUserPhone());
        }
        throw new IllegalArgumentException("设备（机号" + machineLabel + "）正在被其他用户使用（" + occupant + "），请稍后再试");
    }

    /**
     * 管理员手动释放设备占用，释放后该设备可再次下单/发令。
     */
    @Transactional
    public void releaseDeviceUsage(Long deviceId) {
        if (deviceId == null) {
            throw new IllegalArgumentException("设备不能为空");
        }
        Long locked = deviceMapper.selectDeviceIdForUpdate(deviceId);
        if (locked == null) {
            throw new IllegalArgumentException("设备不存在");
        }
        ActiveDeviceUsageRow active = orderMapper.findActiveUsageByDeviceId(deviceId);
        if (active == null) {
            throw new IllegalArgumentException("设备当前未在使用中，无需刷新");
        }
        int affected = orderMapper.releaseActiveUsage(active.getUsageId());
        if (affected == 0) {
            throw new IllegalArgumentException("释放失败，请稍后重试");
        }
    }

    public boolean isDeviceInUse(Long deviceId) {
        if (deviceId == null) {
            return false;
        }
        return orderMapper.findActiveUsageByDeviceId(deviceId) != null;
    }

    private static String formatMachineLabel(String machineNo) {
        if (machineNo == null || machineNo.isBlank()) {
            return "未知";
        }
        return machineNo.trim();
    }

    private static String maskPhone(String phone) {
        if (phone == null || phone.length() < 7) {
            return "其他用户";
        }
        return phone.substring(0, 3) + "****" + phone.substring(phone.length() - 4);
    }
}
