package com.example.springbootdemo.device.service;

import com.example.springbootdemo.auth.dao.UserMapper;
import com.example.springbootdemo.device.AdminDeviceUpsertRequest;
import com.example.springbootdemo.device.AdminDeviceVO;
import com.example.springbootdemo.device.DeviceOptionVO;
import com.example.springbootdemo.device.dao.AdminDeviceRow;
import com.example.springbootdemo.device.dao.DeviceEntity;
import com.example.springbootdemo.device.dao.DeviceMapper;
import com.example.springbootdemo.device.dao.TeacherDeviceBindMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class DeviceServiceImpl implements DeviceService {

    private final DeviceMapper deviceMapper;
    private final UserMapper userMapper;
    private final TeacherDeviceBindMapper teacherDeviceBindMapper;

    public DeviceServiceImpl(
            DeviceMapper deviceMapper,
            UserMapper userMapper,
            TeacherDeviceBindMapper teacherDeviceBindMapper
    ) {
        this.deviceMapper = deviceMapper;
        this.userMapper = userMapper;
        this.teacherDeviceBindMapper = teacherDeviceBindMapper;
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

    @Override
    public List<AdminDeviceVO> listForAdmin(Long merchantId, String keyword, int pageNo, int pageSize) {
        int offset = (pageNo - 1) * pageSize;
        List<AdminDeviceRow> rows = deviceMapper.listForAdmin(merchantId, keyword, offset, pageSize);
        return rows.stream().map(this::toAdminVO).toList();
    }

    @Override
    public long countForAdmin(Long merchantId, String keyword) {
        return deviceMapper.countForAdmin(merchantId, keyword);
    }

    @Override
    public AdminDeviceVO createDevice(AdminDeviceUpsertRequest request) {
        if (request.getMerchantId() == null || request.getMerchantId() <= 0) {
            throw new IllegalArgumentException("商家不能为空");
        }
        if (userMapper.findMerchantById(request.getMerchantId()) == null) {
            throw new IllegalArgumentException("商家不存在");
        }
        DeviceEntity entity = new DeviceEntity();
        entity.setMachineNo(request.getMachineNo());
        entity.setDeviceName(request.getDeviceName());
        entity.setMerchantId(request.getMerchantId());
        entity.setStatus(1);
        entity.setFreeUseDeadline(request.getFreeUseDeadline());
        deviceMapper.insert(entity);
        DeviceEntity created = deviceMapper.findById(entity.getId());
        String merchantName = userMapper.findMerchantById(created.getMerchantId()).getName();
        return new AdminDeviceVO(created.getId(), created.getMachineNo(), created.getDeviceName(), created.getStatus(),
                created.getMerchantId(), merchantName, created.getFreeUseDeadline());
    }

    @Override
    public void updateFreeUseDeadline(Long id, LocalDateTime deadline) {
        DeviceEntity entity = deviceMapper.findById(id);
        if (entity == null) {
            throw new IllegalArgumentException("设备不存在");
        }
        deviceMapper.updateFreeUseDeadline(id, deadline);
    }

    @Override
    public void disableDevice(Long id) {
        DeviceEntity entity = deviceMapper.findById(id);
        if (entity == null) {
            throw new IllegalArgumentException("设备不存在");
        }
        deviceMapper.disableById(id);
    }

    @Override
    public void enableDevice(Long id) {
        DeviceEntity entity = deviceMapper.findById(id);
        if (entity == null) {
            throw new IllegalArgumentException("设备不存在");
        }
        deviceMapper.enableById(id);
    }

    @Override
    @Transactional
    public void unbindMerchant(Long id) {
        DeviceEntity entity = deviceMapper.findById(id);
        if (entity == null) {
            throw new IllegalArgumentException("设备不存在");
        }
        if (entity.getMerchantId() == null || entity.getMerchantId() <= 0) {
            throw new IllegalArgumentException("设备未绑定商家");
        }
        teacherDeviceBindMapper.deleteByDeviceId(id);
        deviceMapper.clearMerchantById(id);
    }

    @Override
    public void bindMerchant(Long id, Long merchantId) {
        if (merchantId == null || merchantId <= 0) {
            throw new IllegalArgumentException("商家不能为空");
        }
        DeviceEntity entity = deviceMapper.findById(id);
        if (entity == null) {
            throw new IllegalArgumentException("设备不存在");
        }
        if (entity.getMerchantId() != null && entity.getMerchantId() > 0) {
            throw new IllegalArgumentException("设备已绑定商家，请先解绑");
        }
        if (userMapper.findMerchantById(merchantId) == null) {
            throw new IllegalArgumentException("商家不存在");
        }
        deviceMapper.updateMerchantById(id, merchantId);
    }

    private AdminDeviceVO toAdminVO(AdminDeviceRow row) {
        return new AdminDeviceVO(row.getId(), row.getMachineNo(), row.getDeviceName(), row.getStatus(),
                row.getMerchantId(), row.getMerchantName(), row.getFreeUseDeadline());
    }
}
