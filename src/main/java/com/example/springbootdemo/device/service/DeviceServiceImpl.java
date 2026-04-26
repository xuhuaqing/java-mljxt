package com.example.springbootdemo.device.service;

import com.example.springbootdemo.auth.dao.UserMapper;
import com.example.springbootdemo.device.DeviceOptionVO;
import com.example.springbootdemo.device.dao.DeviceMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DeviceServiceImpl implements DeviceService {

    private final DeviceMapper deviceMapper;
    private final UserMapper userMapper;

    public DeviceServiceImpl(DeviceMapper deviceMapper, UserMapper userMapper) {
        this.deviceMapper = deviceMapper;
        this.userMapper = userMapper;
    }

    @Override
    public List<DeviceOptionVO> getDevicesByMerchantId(Long merchantId) {
        if (merchantId == null || merchantId <= 0) {
            throw new IllegalArgumentException("merchantId不能为空且必须大于0");
        }
        if (userMapper.findMerchantById(merchantId) == null) {
            throw new IllegalArgumentException("商家不存在");
        }
        return deviceMapper.findByMerchantId(merchantId).stream()
                .map(d -> new DeviceOptionVO(
                        d.getId(),
                        d.getMachineNo(),
                        d.getDeviceName(),
                        d.getStatus(),
                        d.getMerchantId(),
                        d.getFreeUseDeadline()
                ))
                .toList();
    }
}
