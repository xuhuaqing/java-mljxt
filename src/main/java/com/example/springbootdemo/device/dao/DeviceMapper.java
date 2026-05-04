package com.example.springbootdemo.device.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Update;

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

    @Select("""
            SELECT
                id,
                machine_no AS machineNo,
                device_name AS deviceName,
                status,
                merchant_id AS merchantId,
                free_use_deadline AS freeUseDeadline
            FROM merchant_device
            WHERE machine_no = #{machineNo}
            LIMIT 1
            """)
    DeviceEntity findByMachineNo(@Param("machineNo") String machineNo);

    @Select("""
            <script>
            SELECT
              d.id,
              d.machine_no AS machineNo,
              d.device_name AS deviceName,
              d.status,
              d.merchant_id AS merchantId,
              u.name AS merchantName,
              d.free_use_deadline AS freeUseDeadline
            FROM merchant_device d
            LEFT JOIN user_account u ON u.id = d.merchant_id
            <where>
              <if test="merchantId != null">
                d.merchant_id = #{merchantId}
              </if>
              <if test="keyword != null and keyword != ''">
                AND (d.machine_no LIKE CONCAT('%', #{keyword}, '%')
                  OR d.device_name LIKE CONCAT('%', #{keyword}, '%'))
              </if>
            </where>
            ORDER BY d.id DESC
            LIMIT #{offset}, #{pageSize}
            </script>
            """)
    List<AdminDeviceRow> listForAdmin(@Param("merchantId") Long merchantId,
                                      @Param("keyword") String keyword,
                                      @Param("offset") int offset,
                                      @Param("pageSize") int pageSize);

    @Select("""
            <script>
            SELECT COUNT(1)
            FROM merchant_device d
            <where>
              <if test="merchantId != null">
                d.merchant_id = #{merchantId}
              </if>
              <if test="keyword != null and keyword != ''">
                AND (d.machine_no LIKE CONCAT('%', #{keyword}, '%')
                  OR d.device_name LIKE CONCAT('%', #{keyword}, '%'))
              </if>
            </where>
            </script>
            """)
    long countForAdmin(@Param("merchantId") Long merchantId, @Param("keyword") String keyword);

    @Insert("""
            INSERT INTO merchant_device(machine_no, device_name, status, merchant_id, free_use_deadline)
            VALUES(#{machineNo}, #{deviceName}, #{status}, #{merchantId}, #{freeUseDeadline})
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(DeviceEntity entity);

    @Update("""
            UPDATE merchant_device
            SET free_use_deadline = #{freeUseDeadline}
            WHERE id = #{id}
            """)
    int updateFreeUseDeadline(@Param("id") Long id, @Param("freeUseDeadline") java.time.LocalDateTime freeUseDeadline);

    @Update("""
            UPDATE merchant_device
            SET status = 0
            WHERE id = #{id}
            """)
    int disableById(@Param("id") Long id);

    @Update("""
            UPDATE merchant_device
            SET status = 1
            WHERE id = #{id}
            """)
    int enableById(@Param("id") Long id);
}
