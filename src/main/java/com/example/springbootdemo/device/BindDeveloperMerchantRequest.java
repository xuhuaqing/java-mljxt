package com.example.springbootdemo.device;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class BindDeveloperMerchantRequest {

    @NotNull(message = "developerId不能为空")
    @Positive(message = "developerId必须大于0")
    private Long developerId;

    @NotNull(message = "merchantId不能为空")
    @Positive(message = "merchantId必须大于0")
    private Long merchantId;

    public Long getDeveloperId() {
        return developerId;
    }

    public void setDeveloperId(Long developerId) {
        this.developerId = developerId;
    }

    public Long getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(Long merchantId) {
        this.merchantId = merchantId;
    }
}
