package com.example.springbootdemo.order;

import java.time.LocalDateTime;

public record OrderUsageRecordVO(
        Long orderId,
        Long userId,
        Long merchantId,
        String deviceName,
        String projectName,
        Integer projectDuration,
        Integer usageCount,
        Integer sportPerformance,
        LocalDateTime createdAt
) {
}
