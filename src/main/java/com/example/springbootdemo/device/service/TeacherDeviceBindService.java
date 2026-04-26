package com.example.springbootdemo.device.service;

import com.example.springbootdemo.device.BindTeacherDeviceRequest;
import com.example.springbootdemo.device.BindTeacherDeviceVO;
import com.example.springbootdemo.device.TeacherBoundDeviceVO;

import java.util.List;

public interface TeacherDeviceBindService {

    BindTeacherDeviceVO bind(BindTeacherDeviceRequest request);

    List<TeacherBoundDeviceVO> listBoundDevices(Long teacherId, Long merchantId);
}
