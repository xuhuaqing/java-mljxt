package com.example.springbootdemo.order;

import java.time.LocalDateTime;

public record OrderUsageRecordVO(
        Long orderId,
        Long userId,
        String userName,
        String userPhone,
        Long merchantId,
        String deviceName,
        String projectName,
        Integer projectDuration,
        Integer usageCount,
        Integer sportPerformance,
        LocalDateTime createdAt
) {
}
