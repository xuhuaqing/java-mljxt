package com.example.springbootdemo.order;

import java.time.LocalDateTime;

public record AdminDeviceUsageRecordVO(
        Long orderId,
        Long merchantId,
        String merchantName,
        Long deviceId,
        String deviceName,
        Long userId,
        String userName,
        String userPhone,
        String projectName,
        Integer usageCount,
        LocalDateTime createdAt
) {
}
