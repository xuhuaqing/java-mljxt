package com.example.springbootdemo.device.dao;

import java.time.LocalDateTime;

public class DeveloperWithdrawRecordEntity {
    private Long id;
    private Long developerId;
    private Long usageCountSnapshot;
    private LocalDateTime createdAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getDeveloperId() {
        return developerId;
    }

    public void setDeveloperId(Long developerId) {
        this.developerId = developerId;
    }

    public Long getUsageCountSnapshot() {
        return usageCountSnapshot;
    }

    public void setUsageCountSnapshot(Long usageCountSnapshot) {
        this.usageCountSnapshot = usageCountSnapshot;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
