package com.example.springbootdemo.auth.controller;

import com.example.springbootdemo.auth.MerchantOptionVO;
import com.example.springbootdemo.auth.service.MerchantService;
import com.example.springbootdemo.common.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/merchant")
public class MerchantController {

    private final MerchantService merchantService;

    public MerchantController(MerchantService merchantService) {
        this.merchantService = merchantService;
    }

    @GetMapping("/options")
    public ApiResponse<List<MerchantOptionVO>> options(
            @RequestParam(value = "keyword", required = false) String keyword
    ) {
        return ApiResponse.success(merchantService.getMerchantOptions(keyword));
    }
}
