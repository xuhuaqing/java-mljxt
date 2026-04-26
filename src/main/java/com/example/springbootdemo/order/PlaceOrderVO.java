package com.example.springbootdemo.order;

public record PlaceOrderVO(
        Long orderId,
        Long userId,
        String phone,
        Long merchantId,
        String projectName,
        Integer projectDuration,
        Integer usageCount,
        boolean newUserCreated,
        String initialPassword
) {
}
