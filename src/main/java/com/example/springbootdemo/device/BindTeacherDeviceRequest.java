package com.example.springbootdemo.device;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class BindTeacherDeviceRequest {

    @NotNull(message = "teacherId不能为空")
    @Positive(message = "teacherId必须大于0")
    private Long teacherId;

    @NotNull(message = "merchantId不能为空")
    @Positive(message = "merchantId必须大于0")
    private Long merchantId;

    @NotNull(message = "deviceId不能为空")
    @Positive(message = "deviceId必须大于0")
    private Long deviceId;

    public Long getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(Long teacherId) {
        this.teacherId = teacherId;
    }

    public Long getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(Long merchantId) {
        this.merchantId = merchantId;
    }

    public Long getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(Long deviceId) {
        this.deviceId = deviceId;
    }
}
