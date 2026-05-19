package com.example.springbootdemo.order;

import java.time.LocalDateTime;

public record AdminOrderRecordVO(
        Long orderId,
        Long userId,
        String userName,
        String userPhone,
        Long merchantId,
        String merchantName,
        Long deviceId,
        String deviceName,
        String projectName,
        Integer projectDuration,
        Integer usageCount,
        LocalDateTime createdAt
) {
}
