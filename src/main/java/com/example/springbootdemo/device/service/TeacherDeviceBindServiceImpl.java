package com.example.springbootdemo.device.service;

import com.example.springbootdemo.auth.dao.UserMapper;
import com.example.springbootdemo.device.BindTeacherDeviceRequest;
import com.example.springbootdemo.device.BindTeacherDeviceVO;
import com.example.springbootdemo.device.TeacherBoundDeviceVO;
import com.example.springbootdemo.device.dao.DeviceEntity;
import com.example.springbootdemo.device.dao.DeviceMapper;
import com.example.springbootdemo.device.dao.TeacherBoundDeviceRow;
import com.example.springbootdemo.device.dao.TeacherDeviceBindMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TeacherDeviceBindServiceImpl implements TeacherDeviceBindService {

    private final UserMapper userMapper;
    private final DeviceMapper deviceMapper;
    private final TeacherDeviceBindMapper teacherDeviceBindMapper;

    public TeacherDeviceBindServiceImpl(
            UserMapper userMapper,
            DeviceMapper deviceMapper,
            TeacherDeviceBindMapper teacherDeviceBindMapper
    ) {
        this.userMapper = userMapper;
        this.deviceMapper = deviceMapper;
        this.teacherDeviceBindMapper = teacherDeviceBindMapper;
    }

    @Override
    @Transactional
    public BindTeacherDeviceVO bind(BindTeacherDeviceRequest request) {
        if (userMapper.findTeacherById(request.getTeacherId()) == null) {
            throw new IllegalArgumentException("老师不存在");
        }
        if (userMapper.findMerchantById(request.getMerchantId()) == null) {
            throw new IllegalArgumentException("商家不存在");
        }

        DeviceEntity device = deviceMapper.findById(request.getDeviceId());
        if (device == null) {
            throw new IllegalArgumentException("设备不存在");
        }
        if (!request.getMerchantId().equals(device.getMerchantId())) {
            throw new IllegalArgumentException("设备不属于该商家");
        }

        int exists = teacherDeviceBindMapper.countByTeacherAndDevice(request.getTeacherId(), request.getDeviceId());
        if (exists > 0) {
            return new BindTeacherDeviceVO(
                    request.getTeacherId(),
                    request.getMerchantId(),
                    request.getDeviceId(),
                    true
            );
        }

        teacherDeviceBindMapper.insert(request.getTeacherId(), request.getDeviceId());
        return new BindTeacherDeviceVO(
                request.getTeacherId(),
                request.getMerchantId(),
                request.getDeviceId(),
                false
        );
    }

    @Override
    public List<TeacherBoundDeviceVO> listBoundDevices(Long teacherId, Long merchantId) {
        if (teacherId == null || teacherId <= 0) {
            throw new IllegalArgumentException("teacherId不能为空且必须大于0");
        }
        if (userMapper.findTeacherById(teacherId) == null) {
            throw new IllegalArgumentException("老师不存在");
        }
        if (merchantId != null) {
            if (merchantId <= 0) {
                throw new IllegalArgumentException("merchantId必须大于0");
            }
            if (userMapper.findMerchantById(merchantId) == null) {
                throw new IllegalArgumentException("商家不存在");
            }
        }

        List<TeacherBoundDeviceRow> rows = teacherDeviceBindMapper.listBoundDevices(teacherId, merchantId);
        return rows.stream()
                .map(r -> new TeacherBoundDeviceVO(
                        r.getBindId(),
                        r.getTeacherId(),
                        r.getMerchantId(),
                        r.getMerchantName(),
                        r.getDeviceId(),
                        r.getMachineNo(),
                        r.getDeviceName(),
                        r.getDeviceStatus(),
                        r.getFreeUseDeadline(),
                        r.getBindTime()
                ))
                .toList();
    }
}
