package com.example.springbootdemo.device.dao;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface DeveloperWithdrawRecordMapper {

    @Insert("""
            INSERT INTO developer_withdraw_record(
                developer_id,
                usage_count_snapshot,
                created_at
            ) VALUES (
                #{developerId},
                #{usageCountSnapshot},
                #{createdAt}
            )
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(DeveloperWithdrawRecordEntity entity);

    @Select("""
            SELECT
                id,
                developer_id AS developerId,
                usage_count_snapshot AS usageCountSnapshot,
                created_at AS createdAt
            FROM developer_withdraw_record
            WHERE developer_id = #{developerId}
            ORDER BY created_at DESC, id DESC
            LIMIT #{offset}, #{pageSize}
            """)
    List<DeveloperWithdrawRecordEntity> queryByDeveloperId(
            @Param("developerId") Long developerId,
            @Param("offset") int offset,
            @Param("pageSize") int pageSize
    );

    @Select("""
            SELECT COUNT(1)
            FROM developer_withdraw_record
            WHERE developer_id = #{developerId}
            """)
    long countByDeveloperId(@Param("developerId") Long developerId);

    @Select("""
            SELECT COUNT(1)
            FROM developer_withdraw_record
            WHERE developer_id = #{developerId}
              AND created_at >= CURDATE()
              AND created_at < DATE_ADD(CURDATE(), INTERVAL 1 DAY)
            """)
    long countTodayByDeveloperId(@Param("developerId") Long developerId);

    @Select("""
            <script>
            SELECT
                r.id,
                r.developer_id AS developerId,
                ua.name AS developerName,
                ua.phone AS developerPhone,
                r.usage_count_snapshot AS usageCountSnapshot,
                r.created_at AS createdAt
            FROM developer_withdraw_record r
            LEFT JOIN user_account ua ON ua.id = r.developer_id AND ua.role = 4
            <where>
              <if test="developerId != null">
                r.developer_id = #{developerId}
              </if>
            </where>
            ORDER BY r.created_at DESC, r.id DESC
            LIMIT #{offset}, #{pageSize}
            </script>
            """)
    List<AdminWithdrawRecordRow> listForAdmin(
            @Param("developerId") Long developerId,
            @Param("offset") int offset,
            @Param("pageSize") int pageSize
    );

    @Select("""
            <script>
            SELECT COUNT(1)
            FROM developer_withdraw_record r
            <where>
              <if test="developerId != null">
                r.developer_id = #{developerId}
              </if>
            </where>
            </script>
            """)
    long countForAdmin(@Param("developerId") Long developerId);
}
