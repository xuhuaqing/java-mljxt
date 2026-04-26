package com.example.springbootdemo.device;

import java.util.List;

public record DeveloperWithdrawPageVO(
        long total,
        int pageNo,
        int pageSize,
        List<DeveloperWithdrawVO> records
) {
}
