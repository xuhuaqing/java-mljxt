package com.example.springbootdemo.device;

public record BindTeacherDeviceVO(
        Long teacherId,
        Long merchantId,
        Long deviceId,
        boolean alreadyBound
) {
}
