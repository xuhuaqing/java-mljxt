package com.example.springbootdemo.device;

import java.time.LocalDateTime;

public record AdminWithdrawRecordVO(
        Long withdrawRecordId,
        Long developerId,
        String developerName,
        String developerPhone,
        Long usageCountSnapshot,
        LocalDateTime createdAt
) {
}
