package com.example.springbootdemo.device;

import java.time.LocalDateTime;

public record DeveloperWithdrawVO(
        Long withdrawRecordId,
        Long developerId,
        Long usageCountSnapshot,
        LocalDateTime createdAt
) {
}
