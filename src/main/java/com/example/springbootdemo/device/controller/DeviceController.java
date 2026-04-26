package com.example.springbootdemo.device.controller;

import com.example.springbootdemo.common.ApiResponse;
import com.example.springbootdemo.device.DeviceOptionVO;
import com.example.springbootdemo.device.service.DeviceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/device")
public class DeviceController {

    private static final Logger log = LoggerFactory.getLogger(DeviceController.class);

    private final DeviceService deviceService;

    public DeviceController(DeviceService deviceService) {
        this.deviceService = deviceService;
    }

    @GetMapping("/list-by-merchant")
    public ApiResponse<List<DeviceOptionVO>> listByMerchant(
            @RequestParam("merchantId") Long merchantId
    ) {
        log.info("调用接口 /api/device/list-by-merchant, merchantId={}", merchantId);
        List<DeviceOptionVO> devices = deviceService.getDevicesByMerchantId(merchantId);
        log.info("查询设备完成 /api/device/list-by-merchant, merchantId={}, size={}", merchantId, devices.size());
        return ApiResponse.success(devices);
    }
}
