package com.example.springbootdemo.order.service;

import com.example.springbootdemo.order.PlaceOrderRequest;
import com.example.springbootdemo.order.OrderUsagePageVO;
import com.example.springbootdemo.order.PlaceOrderVO;
import com.example.springbootdemo.order.AdminDeviceUsageRecordVO;

import java.util.List;

public interface OrderService {

    PlaceOrderVO placeOrder(PlaceOrderRequest request);

    OrderUsagePageVO queryOrderRecords(String phone, Long userId, Long merchantId, Long deviceId, Integer pageNo, Integer pageSize);

    OrderUsagePageVO queryUsageRecords(String phone, Long userId, Long deviceId, Integer pageNo, Integer pageSize);

    List<AdminDeviceUsageRecordVO> queryAdminDeviceUsageRecords(Long merchantId, Long deviceId, int pageNo, int pageSize);

    long countAdminDeviceUsageRecords(Long merchantId, Long deviceId);

    List<AdminDeviceUsageRecordVO> queryAdminDeviceUsageRecordsForExport(Long merchantId, Long deviceId);
}
