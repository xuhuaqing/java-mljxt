package com.example.springbootdemo.device;

import java.time.LocalDateTime;

public record AdminDeviceVO(
        Long id,
        String machineNo,
        String deviceName,
        Integer status,
        Long merchantId,
        String merchantName,
        LocalDateTime freeUseDeadline
) {
}
