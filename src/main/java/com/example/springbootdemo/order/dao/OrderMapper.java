package com.example.springbootdemo.order.dao;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface OrderMapper {

    @Select("""
            SELECT
                id,
                user_id AS userId,
                merchant_id AS merchantId,
                device_id AS deviceId,
                gender,
                age,
                height,
                weight,
                sport_performance AS sportPerformance,
                project_name AS projectName,
                project_duration AS projectDuration,
                usage_count AS usageCount,
                created_at AS createdAt
            FROM order_record
            WHERE id = #{id}
            LIMIT 1
            """)
    OrderEntity findById(@Param("id") Long id);

    @Select("""
            SELECT
                o.id AS usageId,
                o.user_id AS userId,
                uu.name AS userName,
                uu.phone AS userPhone,
                md.machine_no AS machineNo,
                o.project_duration AS projectDuration,
                o.created_at AS createdAt
            FROM usage_record o
            INNER JOIN merchant_device md ON md.id = o.device_id
            LEFT JOIN user_account uu ON uu.id = o.user_id
            WHERE o.device_id = #{deviceId}
              AND DATE_ADD(o.created_at, INTERVAL COALESCE(o.project_duration, 40) MINUTE) > NOW()
            ORDER BY o.created_at DESC, o.id DESC
            LIMIT 1
            """)
    ActiveDeviceUsageRow findActiveUsageByDeviceId(@Param("deviceId") Long deviceId);

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

    @Update("""
            UPDATE order_record
            SET usage_count = usage_count - 1
            WHERE id = #{id}
              AND usage_count > 0
            """)
    int decrementOrderUsageCount(@Param("id") Long id);

    @Select("""
            <script>
            SELECT
                o.id,
                o.user_id AS userId,
                uu.name AS userName,
                uu.phone AS userPhone,
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
            LEFT JOIN user_account uu ON uu.id = o.user_id
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
              <if test="merchantId != null">
                AND o.merchant_id = #{merchantId}
              </if>
              <if test="deviceId != null">
                AND o.device_id = #{deviceId}
              </if>
            ORDER BY o.created_at DESC, o.id DESC
            LIMIT #{offset}, #{pageSize}
            </script>
            """)
    List<OrderEntity> queryOrderRecords(
            @Param("phone") String phone,
            @Param("userId") Long userId,
            @Param("merchantId") Long merchantId,
            @Param("deviceId") Long deviceId,
            @Param("offset") int offset,
            @Param("pageSize") int pageSize
    );

    @Select("""
            <script>
            SELECT COUNT(1)
            FROM order_record o
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
              <if test="merchantId != null">
                AND o.merchant_id = #{merchantId}
              </if>
              <if test="deviceId != null">
                AND o.device_id = #{deviceId}
              </if>
            </script>
            """)
    long countOrderRecords(
            @Param("phone") String phone,
            @Param("userId") Long userId,
            @Param("merchantId") Long merchantId,
            @Param("deviceId") Long deviceId
    );

    @Insert("""
            INSERT INTO usage_record(
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
    int insertUsage(OrderEntity order);

    @Select("""
            <script>
            SELECT
                o.id,
                o.user_id AS userId,
                uu.name AS userName,
                uu.phone AS userPhone,
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
            FROM usage_record o
            LEFT JOIN merchant_device md ON md.id = o.device_id
            LEFT JOIN user_account uu ON uu.id = o.user_id
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
            FROM usage_record
            WHERE 1 = 1
              <if test="userId != null">
                AND user_id = #{userId}
              </if>
              <if test="phone != null and phone != ''">
                AND EXISTS (
                  SELECT 1
                  FROM user_account ua
                  WHERE ua.id = usage_record.user_id
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
            FROM usage_record
            WHERE merchant_id IN
            <foreach item="merchantId" collection="merchantIds" open="(" separator="," close=")">
                #{merchantId}
            </foreach>
            GROUP BY merchant_id
            </script>
            """)
    List<MerchantUsageStatRow> statMerchantTotalUsageCount(@Param("merchantIds") List<Long> merchantIds);

    @Select("""
            <script>
            SELECT
              o.id AS orderId,
              o.merchant_id AS merchantId,
              ua.name AS merchantName,
              o.device_id AS deviceId,
              md.device_name AS deviceName,
              o.user_id AS userId,
              uu.name AS userName,
              uu.phone AS userPhone,
              o.project_name AS projectName,
              o.usage_count AS usageCount,
              o.created_at AS createdAt
            FROM usage_record o
            LEFT JOIN user_account ua ON ua.id = o.merchant_id
            LEFT JOIN merchant_device md ON md.id = o.device_id
            LEFT JOIN user_account uu ON uu.id = o.user_id
            WHERE 1=1
              <if test="merchantId != null">
                AND o.merchant_id = #{merchantId}
              </if>
              <if test="deviceId != null">
                AND o.device_id = #{deviceId}
              </if>
            ORDER BY o.created_at DESC, o.id DESC
            LIMIT #{offset}, #{pageSize}
            </script>
            """)
    List<com.example.springbootdemo.order.AdminDeviceUsageRecordVO> queryAdminDeviceUsageRecords(
            @Param("merchantId") Long merchantId,
            @Param("deviceId") Long deviceId,
            @Param("offset") int offset,
            @Param("pageSize") int pageSize
    );

    @Select("""
            <script>
            SELECT COUNT(1)
            FROM usage_record o
            WHERE 1=1
              <if test="merchantId != null">
                AND o.merchant_id = #{merchantId}
              </if>
              <if test="deviceId != null">
                AND o.device_id = #{deviceId}
              </if>
            </script>
            """)
    long countAdminDeviceUsageRecords(@Param("merchantId") Long merchantId, @Param("deviceId") Long deviceId);

    @Select("""
            <script>
            SELECT
              o.id AS orderId,
              o.merchant_id AS merchantId,
              ua.name AS merchantName,
              o.device_id AS deviceId,
              md.device_name AS deviceName,
              o.user_id AS userId,
              uu.name AS userName,
              uu.phone AS userPhone,
              o.project_name AS projectName,
              o.usage_count AS usageCount,
              o.created_at AS createdAt
            FROM usage_record o
            LEFT JOIN user_account ua ON ua.id = o.merchant_id
            LEFT JOIN merchant_device md ON md.id = o.device_id
            LEFT JOIN user_account uu ON uu.id = o.user_id
            WHERE 1=1
              <if test="merchantId != null">
                AND o.merchant_id = #{merchantId}
              </if>
              <if test="deviceId != null">
                AND o.device_id = #{deviceId}
              </if>
            ORDER BY o.created_at DESC, o.id DESC
            LIMIT 5000
            </script>
            """)
    List<com.example.springbootdemo.order.AdminDeviceUsageRecordVO> queryAdminDeviceUsageRecordsForExport(
            @Param("merchantId") Long merchantId,
            @Param("deviceId") Long deviceId
    );

    @Select("""
            <script>
            SELECT
              o.id AS orderId,
              o.user_id AS userId,
              uu.name AS userName,
              uu.phone AS userPhone,
              o.merchant_id AS merchantId,
              um.name AS merchantName,
              o.device_id AS deviceId,
              md.device_name AS deviceName,
              o.project_name AS projectName,
              o.project_duration AS projectDuration,
              o.usage_count AS usageCount,
              o.created_at AS createdAt
            FROM order_record o
            LEFT JOIN user_account uu ON uu.id = o.user_id
            LEFT JOIN user_account um ON um.id = o.merchant_id
            LEFT JOIN merchant_device md ON md.id = o.device_id
            WHERE 1=1
              <if test="merchantId != null">
                AND o.merchant_id = #{merchantId}
              </if>
              <if test="deviceId != null">
                AND o.device_id = #{deviceId}
              </if>
              <if test="phone != null and phone != ''">
                AND uu.phone = #{phone}
              </if>
            ORDER BY o.created_at DESC, o.id DESC
            LIMIT #{offset}, #{pageSize}
            </script>
            """)
    List<com.example.springbootdemo.order.AdminOrderRecordVO> queryAdminOrderRecords(
            @Param("merchantId") Long merchantId,
            @Param("deviceId") Long deviceId,
            @Param("phone") String phone,
            @Param("offset") int offset,
            @Param("pageSize") int pageSize
    );

    @Select("""
            <script>
            SELECT COUNT(1)
            FROM order_record o
            LEFT JOIN user_account uu ON uu.id = o.user_id
            WHERE 1=1
              <if test="merchantId != null">
                AND o.merchant_id = #{merchantId}
              </if>
              <if test="deviceId != null">
                AND o.device_id = #{deviceId}
              </if>
              <if test="phone != null and phone != ''">
                AND uu.phone = #{phone}
              </if>
            </script>
            """)
    long countAdminOrderRecords(
            @Param("merchantId") Long merchantId,
            @Param("deviceId") Long deviceId,
            @Param("phone") String phone
    );
}
