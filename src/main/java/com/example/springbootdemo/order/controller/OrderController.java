package com.example.springbootdemo.order.controller;

import com.example.springbootdemo.common.ApiResponse;
import com.example.springbootdemo.order.OrderUsagePageVO;
import com.example.springbootdemo.order.PlaceOrderRequest;
import com.example.springbootdemo.order.PlaceOrderVO;
import com.example.springbootdemo.order.service.OrderService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/order")
public class OrderController {

    private static final Logger log = LoggerFactory.getLogger(OrderController.class);

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/create")
    public ApiResponse<PlaceOrderVO> create(@Valid @RequestBody PlaceOrderRequest request) {
        log.info("调用接口 /api/order/create, phone={}, merchantId={}", request.getPhone(), request.getMerchantId());
        PlaceOrderVO result = orderService.placeOrder(request);
        log.info("下单成功 /api/order/create, orderId={}, userId={}, newUserCreated={}",
                result.orderId(), result.userId(), result.newUserCreated());
        return ApiResponse.success(result);
    }

    @GetMapping("/usage-records")
    public ApiResponse<OrderUsagePageVO> usageRecords(
            @RequestParam(value = "phone", required = false) String phone,
            @RequestParam(value = "userId", required = false) Long userId,
            @RequestParam(value = "deviceId", required = false) Long deviceId,
            @RequestParam(value = "pageNo", required = false) Integer pageNo,
            @RequestParam(value = "pageSize", required = false) Integer pageSize
    ) {
        log.info("调用接口 /api/order/usage-records, phone={}, userId={}, deviceId={}, pageNo={}, pageSize={}",
                phone, userId, deviceId, pageNo, pageSize);
        OrderUsagePageVO result = orderService.queryUsageRecords(phone, userId, deviceId, pageNo, pageSize);
        log.info("查询使用记录完成 /api/order/usage-records, total={}, pageNo={}, pageSize={}",
                result.total(), result.pageNo(), result.pageSize());
        return ApiResponse.success(result);
    }
}
