package com.example.springbootdemo.order;

import java.util.List;

public record OrderUsagePageVO(
        long total,
        int pageNo,
        int pageSize,
        List<OrderUsageRecordVO> records
) {
}
