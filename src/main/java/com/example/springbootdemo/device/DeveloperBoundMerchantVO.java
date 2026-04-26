package com.example.springbootdemo.device;

import java.time.LocalDateTime;

public record DeveloperBoundMerchantVO(
        Long bindId,
        Long developerId,
        Long merchantId,
        String merchantName,
        String merchantPhone,
        Integer remainingUseCount,
        LocalDateTime bindTime
) {
}
