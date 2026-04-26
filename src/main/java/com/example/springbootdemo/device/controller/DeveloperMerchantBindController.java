package com.example.springbootdemo.device.controller;

import com.example.springbootdemo.common.ApiResponse;
import com.example.springbootdemo.device.BindDeveloperMerchantRequest;
import com.example.springbootdemo.device.BindDeveloperMerchantVO;
import com.example.springbootdemo.device.DeveloperBoundDeviceVO;
import com.example.springbootdemo.device.DeveloperWithdrawPageVO;
import com.example.springbootdemo.device.DeveloperWithdrawRequest;
import com.example.springbootdemo.device.DeveloperWithdrawVO;
import com.example.springbootdemo.device.service.DeveloperMerchantBindService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/developer-merchant")
public class DeveloperMerchantBindController {

    private static final Logger log = LoggerFactory.getLogger(DeveloperMerchantBindController.class);

    private final DeveloperMerchantBindService developerMerchantBindService;

    public DeveloperMerchantBindController(DeveloperMerchantBindService developerMerchantBindService) {
        this.developerMerchantBindService = developerMerchantBindService;
    }

    @PostMapping("/bind")
    public ApiResponse<BindDeveloperMerchantVO> bind(@Valid @RequestBody BindDeveloperMerchantRequest request) {
        log.info("调用接口 /api/developer-merchant/bind, developerId={}, merchantId={}",
                request.getDeveloperId(), request.getMerchantId());
        BindDeveloperMerchantVO result = developerMerchantBindService.bind(request);
        log.info("绑定结果 /api/developer-merchant/bind, developerId={}, merchantId={}, alreadyBound={}",
                result.developerId(), result.merchantId(), result.alreadyBound());
        return ApiResponse.success(result);
    }

    @GetMapping("/bound-list")
    public ApiResponse<List<DeveloperBoundDeviceVO>> boundList(@RequestParam("developerId") Long developerId) {
        log.info("调用接口 /api/developer-merchant/bound-list, developerId={}", developerId);
        List<DeveloperBoundDeviceVO> result = developerMerchantBindService.listBoundMerchants(developerId);
        log.info("查询绑定设备完成 /api/developer-merchant/bound-list, developerId={}, size={}",
                developerId, result.size());
        return ApiResponse.success(result);
    }

    @PostMapping("/withdraw")
    public ApiResponse<DeveloperWithdrawVO> withdraw(@Valid @RequestBody DeveloperWithdrawRequest request) {
        log.info("调用接口 /api/developer-merchant/withdraw, developerId={}", request.getDeveloperId());
        DeveloperWithdrawVO result = developerMerchantBindService.withdraw(request);
        log.info("提现记录生成成功 /api/developer-merchant/withdraw, recordId={}, developerId={}, usageCountSnapshot={}",
                result.withdrawRecordId(), result.developerId(), result.usageCountSnapshot());
        return ApiResponse.success(result);
    }

    @GetMapping("/withdraw-records")
    public ApiResponse<DeveloperWithdrawPageVO> withdrawRecords(
            @RequestParam("developerId") Long developerId,
            @RequestParam(value = "pageNo", required = false) Integer pageNo,
            @RequestParam(value = "pageSize", required = false) Integer pageSize
    ) {
        log.info("调用接口 /api/developer-merchant/withdraw-records, developerId={}, pageNo={}, pageSize={}",
                developerId, pageNo, pageSize);
        DeveloperWithdrawPageVO result = developerMerchantBindService.listWithdrawRecords(developerId, pageNo, pageSize);
        log.info("查询提现记录完成 /api/developer-merchant/withdraw-records, developerId={}, total={}",
                developerId, result.total());
        return ApiResponse.success(result);
    }
}
