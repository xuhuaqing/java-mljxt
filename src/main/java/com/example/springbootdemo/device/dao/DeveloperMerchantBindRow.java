package com.example.springbootdemo.device.dao;

import java.time.LocalDateTime;

public class DeveloperMerchantBindRow {
    private Long bindId;
    private Long developerId;
    private Long merchantId;
    private String merchantName;
    private String merchantPhone;
    private Integer remainingUseCount;
    private LocalDateTime bindTime;

    public Long getBindId() {
        return bindId;
    }

    public void setBindId(Long bindId) {
        this.bindId = bindId;
    }

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

    public String getMerchantName() {
        return merchantName;
    }

    public void setMerchantName(String merchantName) {
        this.merchantName = merchantName;
    }

    public String getMerchantPhone() {
        return merchantPhone;
    }

    public void setMerchantPhone(String merchantPhone) {
        this.merchantPhone = merchantPhone;
    }

    public Integer getRemainingUseCount() {
        return remainingUseCount;
    }

    public void setRemainingUseCount(Integer remainingUseCount) {
        this.remainingUseCount = remainingUseCount;
    }

    public LocalDateTime getBindTime() {
        return bindTime;
    }

    public void setBindTime(LocalDateTime bindTime) {
        this.bindTime = bindTime;
    }
}
