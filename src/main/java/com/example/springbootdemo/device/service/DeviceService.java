package com.example.springbootdemo.device.service;

import com.example.springbootdemo.device.DeviceOptionVO;

import java.util.List;

public interface DeviceService {

    List<DeviceOptionVO> getDevicesByMerchantId(Long merchantId);
}
