package com.example.springbootdemo.device.dao;

import java.time.LocalDateTime;

public class TeacherBoundDeviceRow {
    private Long bindId;
    private Long teacherId;
    private Long merchantId;
    private String merchantName;
    private Long deviceId;
    private String machineNo;
    private String deviceName;
    private Integer deviceStatus;
    private LocalDateTime freeUseDeadline;
    private LocalDateTime bindTime;

    public Long getBindId() {
        return bindId;
    }

    public void setBindId(Long bindId) {
        this.bindId = bindId;
    }

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

    public String getMerchantName() {
        return merchantName;
    }

    public void setMerchantName(String merchantName) {
        this.merchantName = merchantName;
    }

    public Long getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(Long deviceId) {
        this.deviceId = deviceId;
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

    public Integer getDeviceStatus() {
        return deviceStatus;
    }

    public void setDeviceStatus(Integer deviceStatus) {
        this.deviceStatus = deviceStatus;
    }

    public LocalDateTime getFreeUseDeadline() {
        return freeUseDeadline;
    }

    public void setFreeUseDeadline(LocalDateTime freeUseDeadline) {
        this.freeUseDeadline = freeUseDeadline;
    }

    public LocalDateTime getBindTime() {
        return bindTime;
    }

    public void setBindTime(LocalDateTime bindTime) {
        this.bindTime = bindTime;
    }
}
