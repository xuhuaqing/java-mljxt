package com.example.springbootdemo.device;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class DeveloperWithdrawRequest {

    @NotNull(message = "developerId不能为空")
    @Positive(message = "developerId必须大于0")
    private Long developerId;

    public Long getDeveloperId() {
        return developerId;
    }

    public void setDeveloperId(Long developerId) {
        this.developerId = developerId;
    }
}
