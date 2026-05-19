package com.example.springbootdemo.device;

import jakarta.validation.constraints.NotNull;

public class BindDeviceMerchantRequest {

    @NotNull(message = "merchantId不能为空")
    private Long merchantId;

    public Long getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(Long merchantId) {
        this.merchantId = merchantId;
    }
}
