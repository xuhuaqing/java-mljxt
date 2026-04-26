package com.example.springbootdemo.device;

public record BindDeveloperMerchantVO(
        Long developerId,
        Long merchantId,
        boolean alreadyBound
) {
}
