package com.example.springbootdemo.device.dao;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Delete;

import java.util.List;

@Mapper
public interface DeveloperMerchantBindMapper {

    @Select("""
            SELECT COUNT(1)
            FROM developer_merchant_bind
            WHERE developer_id = #{developerId}
              AND merchant_id = #{merchantId}
            """)
    int countByDeveloperAndMerchant(@Param("developerId") Long developerId, @Param("merchantId") Long merchantId);

    @Insert("""
            INSERT INTO developer_merchant_bind(developer_id, merchant_id)
            VALUES(#{developerId}, #{merchantId})
            """)
    int insert(@Param("developerId") Long developerId, @Param("merchantId") Long merchantId);

    @Delete("""
            DELETE FROM developer_merchant_bind
            WHERE developer_id = #{developerId}
              AND merchant_id = #{merchantId}
            """)
    int deleteByDeveloperAndMerchant(@Param("developerId") Long developerId, @Param("merchantId") Long merchantId);

    @Select("""
            SELECT
                dmb.id AS bindId,
                dmb.developer_id AS developerId,
                ua.id AS merchantId,
                ua.name AS merchantName,
                ua.phone AS merchantPhone,
                ua.remaining_use_count AS remainingUseCount,
                dmb.created_at AS bindTime
            FROM developer_merchant_bind dmb
            INNER JOIN user_account ua ON ua.id = dmb.merchant_id
            WHERE dmb.developer_id = #{developerId}
            ORDER BY dmb.id DESC
            LIMIT 500
            """)
    List<DeveloperMerchantBindRow> listByDeveloperId(@Param("developerId") Long developerId);

    @Select("""
            SELECT COALESCE(SUM(o.usage_count), 0)
            FROM developer_merchant_bind dmb
            LEFT JOIN usage_record o ON o.merchant_id = dmb.merchant_id
            WHERE dmb.developer_id = #{developerId}
            """)
    long sumUsageCountByDeveloperId(@Param("developerId") Long developerId);
}
