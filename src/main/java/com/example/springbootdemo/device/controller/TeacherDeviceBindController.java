package com.example.springbootdemo.device.controller;

import com.example.springbootdemo.common.ApiResponse;
import com.example.springbootdemo.device.BindTeacherDeviceRequest;
import com.example.springbootdemo.device.BindTeacherDeviceVO;
import com.example.springbootdemo.device.TeacherBoundDeviceVO;
import com.example.springbootdemo.device.service.TeacherDeviceBindService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/teacher-device")
public class TeacherDeviceBindController {

    private static final Logger log = LoggerFactory.getLogger(TeacherDeviceBindController.class);

    private final TeacherDeviceBindService teacherDeviceBindService;

    public TeacherDeviceBindController(TeacherDeviceBindService teacherDeviceBindService) {
        this.teacherDeviceBindService = teacherDeviceBindService;
    }

    @PostMapping("/bind")
    public ApiResponse<BindTeacherDeviceVO> bind(@Valid @RequestBody BindTeacherDeviceRequest request) {
        log.info("调用接口 /api/teacher-device/bind, teacherId={}, merchantId={}, deviceId={}",
                request.getTeacherId(), request.getMerchantId(), request.getDeviceId());
        BindTeacherDeviceVO result = teacherDeviceBindService.bind(request);
        log.info("绑定结果 /api/teacher-device/bind, teacherId={}, deviceId={}, alreadyBound={}",
                result.teacherId(), result.deviceId(), result.alreadyBound());
        return ApiResponse.success(result);
    }

    @GetMapping("/bound-list")
    public ApiResponse<List<TeacherBoundDeviceVO>> boundList(
            @RequestParam("teacherId") Long teacherId,
            @RequestParam(value = "merchantId", required = false) Long merchantId
    ) {
        log.info("调用接口 /api/teacher-device/bound-list, teacherId={}, merchantId={}", teacherId, merchantId);
        List<TeacherBoundDeviceVO> result = teacherDeviceBindService.listBoundDevices(teacherId, merchantId);
        log.info("查询绑定设备完成 /api/teacher-device/bound-list, teacherId={}, size={}", teacherId, result.size());
        return ApiResponse.success(result);
    }
}
