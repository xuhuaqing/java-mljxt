package com.example.springbootdemo.device;

import java.util.List;

public record AdminWithdrawRecordPageVO(
        long total,
        int pageNo,
        int pageSize,
        List<AdminWithdrawRecordVO> records
) {
}
