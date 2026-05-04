package com.example.springbootdemo.device;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public class AdminDeviceUpsertRequest {

    @NotBlank(message = "机器编号不能为空")
    private String machineNo;

    @NotBlank(message = "设备名称不能为空")
    private String deviceName;

    @NotNull(message = "商家不能为空")
    private Long merchantId;

    private LocalDateTime freeUseDeadline;

    public String getMachineNo() {
        return machineNo;
    }

    public void setMachineNo(String machineNo) {
        this.machineNo = machineNo;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public Long getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(Long merchantId) {
        this.merchantId = merchantId;
    }

    public LocalDateTime getFreeUseDeadline() {
        return freeUseDeadline;
    }

    public void setFreeUseDeadline(LocalDateTime freeUseDeadline) {
        this.freeUseDeadline = freeUseDeadline;
    }
}
