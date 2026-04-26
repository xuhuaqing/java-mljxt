package com.example.springbootdemo.device.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface DeviceMapper {

    @Select("""
            SELECT
                id,
                machine_no AS machineNo,
                device_name AS deviceName,
                status,
                merchant_id AS merchantId,
                free_use_deadline AS freeUseDeadline
            FROM merchant_device
            WHERE merchant_id = #{merchantId}
            ORDER BY id DESC
            LIMIT 500
            """)
    List<DeviceEntity> findByMerchantId(@Param("merchantId") Long merchantId);

    @Select("""
            SELECT
                id,
                machine_no AS machineNo,
                device_name AS deviceName,
                status,
                merchant_id AS merchantId,
                free_use_deadline AS freeUseDeadline
            FROM merchant_device
            WHERE id = #{deviceId}
            LIMIT 1
            """)
    DeviceEntity findById(@Param("deviceId") Long deviceId);
}
