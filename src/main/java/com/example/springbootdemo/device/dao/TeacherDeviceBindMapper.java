package com.example.springbootdemo.device.dao;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface TeacherDeviceBindMapper {

    @Select("""
            SELECT COUNT(1)
            FROM teacher_device_bind
            WHERE teacher_id = #{teacherId}
              AND device_id = #{deviceId}
            """)
    int countByTeacherAndDevice(@Param("teacherId") Long teacherId, @Param("deviceId") Long deviceId);

    @Insert("""
            INSERT INTO teacher_device_bind(teacher_id, device_id)
            VALUES(#{teacherId}, #{deviceId})
            """)
    int insert(@Param("teacherId") Long teacherId, @Param("deviceId") Long deviceId);

    @Delete("""
            DELETE FROM teacher_device_bind
            WHERE device_id = #{deviceId}
            """)
    int deleteByDeviceId(@Param("deviceId") Long deviceId);

    @Select("""
            <script>
            SELECT
                tdb.id AS bindId,
                tdb.teacher_id AS teacherId,
                md.merchant_id AS merchantId,
                ua.name AS merchantName,
                md.id AS deviceId,
                md.machine_no AS machineNo,
                md.device_name AS deviceName,
                md.status AS deviceStatus,
                md.free_use_deadline AS freeUseDeadline,
                tdb.created_at AS bindTime
            FROM teacher_device_bind tdb
            INNER JOIN merchant_device md ON md.id = tdb.device_id
            INNER JOIN user_account ua ON ua.id = md.merchant_id
            WHERE tdb.teacher_id = #{teacherId}
            <if test="merchantId != null">
              AND md.merchant_id = #{merchantId}
            </if>
            ORDER BY tdb.id DESC
            LIMIT 500
            </script>
            """)
    List<TeacherBoundDeviceRow> listBoundDevices(
            @Param("teacherId") Long teacherId,
            @Param("merchantId") Long merchantId
    );
}
