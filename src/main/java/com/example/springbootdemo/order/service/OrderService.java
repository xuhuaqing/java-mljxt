package com.example.springbootdemo.order.service;

import com.example.springbootdemo.order.PlaceOrderRequest;
import com.example.springbootdemo.order.OrderUsagePageVO;
import com.example.springbootdemo.order.PlaceOrderVO;

public interface OrderService {

    PlaceOrderVO placeOrder(PlaceOrderRequest request);

    OrderUsagePageVO queryUsageRecords(String phone, Long userId, Long deviceId, Integer pageNo, Integer pageSize);
}
