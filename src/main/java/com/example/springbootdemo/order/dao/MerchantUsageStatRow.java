package com.example.springbootdemo.order.dao;

public class MerchantUsageStatRow {
    private Long merchantId;
    private Long totalUsageCount;

    public Long getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(Long merchantId) {
        this.merchantId = merchantId;
    }

    public Long getTotalUsageCount() {
        return totalUsageCount;
    }

    public void setTotalUsageCount(Long totalUsageCount) {
        this.totalUsageCount = totalUsageCount;
    }
}
