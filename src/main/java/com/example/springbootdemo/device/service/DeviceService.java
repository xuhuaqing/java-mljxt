package com.example.springbootdemo.device.service;

import com.example.springbootdemo.device.AdminDeviceUpsertRequest;
import com.example.springbootdemo.device.AdminDeviceVO;
import com.example.springbootdemo.device.DeviceOptionVO;

import java.util.List;
import java.time.LocalDateTime;

public interface DeviceService {

    List<DeviceOptionVO> getDevicesByMerchantId(Long merchantId);

    List<AdminDeviceVO> listForAdmin(Long merchantId, String keyword, int pageNo, int pageSize);

    long countForAdmin(Long merchantId, String keyword);

    AdminDeviceVO createDevice(AdminDeviceUpsertRequest request);

    void updateFreeUseDeadline(Long id, LocalDateTime deadline);

    void disableDevice(Long id);

    void enableDevice(Long id);
}
