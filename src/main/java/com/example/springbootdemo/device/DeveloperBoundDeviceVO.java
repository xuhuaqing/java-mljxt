package com.example.springbootdemo.device;

import java.time.LocalDateTime;

public record DeveloperBoundDeviceVO(
        Long bindId,
        Long developerId,
        Long merchantId,
        String merchantName,
        String merchantPhone,
        Integer remainingUseCount,
        Long merchantTotalDeviceUsageCount,
        Long deviceId,
        String machineNo,
        String deviceName,
        Integer deviceStatus,
        LocalDateTime freeUseDeadline,
        LocalDateTime bindTime
) {
}
