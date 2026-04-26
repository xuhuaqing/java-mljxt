package com.example.springbootdemo.device;

import java.time.LocalDateTime;

public record DeviceOptionVO(
        Long id,
        String machineNo,
        String deviceName,
        Integer status,
        Long merchantId,
        LocalDateTime freeUseDeadline
) {
}
