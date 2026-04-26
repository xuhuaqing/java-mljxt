package com.example.springbootdemo.device;

import java.time.LocalDateTime;

public record TeacherBoundDeviceVO(
        Long bindId,
        Long teacherId,
        Long merchantId,
        String merchantName,
        Long deviceId,
        String machineNo,
        String deviceName,
        Integer deviceStatus,
        LocalDateTime freeUseDeadline,
        LocalDateTime bindTime
) {
}
