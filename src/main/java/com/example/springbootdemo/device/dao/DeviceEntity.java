package com.example.springbootdemo.device.dao;

import java.time.LocalDateTime;

public class DeviceEntity {
    private Long id;
    private String machineNo;
    private String deviceName;
    private Integer status;
    private Long merchantId;
    private LocalDateTime freeUseDeadline;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
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
