package com.example.springbootdemo.auth.service;

import com.example.springbootdemo.auth.MerchantOptionVO;

import java.util.List;

public interface MerchantService {
    List<MerchantOptionVO> getMerchantOptions(String keyword);
}
