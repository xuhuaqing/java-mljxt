package com.example.springbootdemo.device.service;

import com.example.springbootdemo.device.BindDeveloperMerchantRequest;
import com.example.springbootdemo.device.BindDeveloperMerchantVO;
import com.example.springbootdemo.device.DeveloperBoundDeviceVO;
import com.example.springbootdemo.device.AdminWithdrawRecordPageVO;
import com.example.springbootdemo.device.DeveloperWithdrawPageVO;
import com.example.springbootdemo.device.DeveloperWithdrawRequest;
import com.example.springbootdemo.device.DeveloperWithdrawVO;

import java.util.List;

public interface DeveloperMerchantBindService {

    BindDeveloperMerchantVO bind(BindDeveloperMerchantRequest request);

    BindDeveloperMerchantVO unbind(BindDeveloperMerchantRequest request);

    List<DeveloperBoundDeviceVO> listBoundMerchants(Long developerId);

    DeveloperWithdrawVO withdraw(DeveloperWithdrawRequest request);

    DeveloperWithdrawPageVO listWithdrawRecords(Long developerId, Integer pageNo, Integer pageSize);

    AdminWithdrawRecordPageVO listWithdrawRecordsForAdmin(Long developerId, Integer pageNo, Integer pageSize);
}
