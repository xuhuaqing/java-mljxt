package com.example.springbootdemo.hardware;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class SendByOrderRequest {

    @NotNull(message = "订单ID不能为空")
    @Min(value = 1, message = "订单ID必须大于0")
    private Long orderId;

    @Min(value = 0, message = "机器编号范围是0-9999")
    @Max(value = 9999, message = "机器编号范围是0-9999")
    private Integer machineNo;

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Integer getMachineNo() {
        return machineNo;
    }

    public void setMachineNo(Integer machineNo) {
        this.machineNo = machineNo;
    }
}

