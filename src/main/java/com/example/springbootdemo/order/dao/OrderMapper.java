package com.example.springbootdemo.order.dao;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface OrderMapper {

    @Insert("""
            INSERT INTO order_record(
                user_id,
                merchant_id,
                device_id,
                gender,
                age,
                height,
                weight,
                sport_performance,
                project_name,
                project_duration,
                usage_count
            ) VALUES (
                #{userId},
                #{merchantId},
                #{deviceId},
                #{gender},
                #{age},
                #{height},
                #{weight},
                #{sportPerformance},
                #{projectName},
                #{projectDuration},
                #{usageCount}
            )
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(OrderEntity order);

    @Select("""
            <script>
            SELECT
                o.id,
                o.user_id AS userId,
                o.merchant_id AS merchantId,
                o.device_id AS deviceId,
                md.device_name AS deviceName,
                o.gender,
                o.age,
                o.height,
                o.weight,
                o.sport_performance AS sportPerformance,
                o.project_name AS projectName,
                o.project_duration AS projectDuration,
                o.usage_count AS usageCount,
                o.created_at AS createdAt
            FROM order_record o
            LEFT JOIN merchant_device md ON md.id = o.device_id
            WHERE 1 = 1
              <if test="userId != null">
                AND o.user_id = #{userId}
              </if>
              <if test="phone != null and phone != ''">
                AND EXISTS (
                  SELECT 1
                  FROM user_account ua
                  WHERE ua.id = o.user_id
                    AND ua.phone = #{phone}
                )
              </if>
              <if test="deviceId != null">
                AND o.device_id = #{deviceId}
              </if>
            ORDER BY o.created_at DESC, o.id DESC
            LIMIT #{offset}, #{pageSize}
            </script>
            """)
    List<OrderEntity> queryUsageRecords(
            @Param("phone") String phone,
            @Param("userId") Long userId,
            @Param("deviceId") Long deviceId,
            @Param("offset") int offset,
            @Param("pageSize") int pageSize
    );

    @Select("""
            <script>
            SELECT COUNT(1)
            FROM order_record
            WHERE 1 = 1
              <if test="userId != null">
                AND user_id = #{userId}
              </if>
              <if test="phone != null and phone != ''">
                AND EXISTS (
                  SELECT 1
                  FROM user_account ua
                  WHERE ua.id = order_record.user_id
                    AND ua.phone = #{phone}
                )
              </if>
              <if test="deviceId != null">
                AND device_id = #{deviceId}
              </if>
            </script>
            """)
    long countUsageRecords(
            @Param("phone") String phone,
            @Param("userId") Long userId,
            @Param("deviceId") Long deviceId
    );

    @Select("""
            <script>
            SELECT
                merchant_id AS merchantId,
                COALESCE(SUM(usage_count), 0) AS totalUsageCount
            FROM order_record
            WHERE merchant_id IN
            <foreach item="merchantId" collection="merchantIds" open="(" separator="," close=")">
                #{merchantId}
            </foreach>
            GROUP BY merchant_id
            </script>
            """)
    List<MerchantUsageStatRow> statMerchantTotalUsageCount(@Param("merchantIds") List<Long> merchantIds);
}
