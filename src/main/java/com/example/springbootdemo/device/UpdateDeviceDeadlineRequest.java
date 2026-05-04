package com.example.springbootdemo.device;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public class UpdateDeviceDeadlineRequest {

    @NotNull(message = "freeUseDeadline不能为空")
    private LocalDateTime freeUseDeadline;

    public LocalDateTime getFreeUseDeadline() {
        return freeUseDeadline;
    }

    public void setFreeUseDeadline(LocalDateTime freeUseDeadline) {
        this.freeUseDeadline = freeUseDeadline;
    }
}
