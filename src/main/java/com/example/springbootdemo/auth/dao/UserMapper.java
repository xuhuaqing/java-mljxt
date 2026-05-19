package com.example.springbootdemo.auth.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface UserMapper {

    @Select("""
            SELECT id, name, phone, password, role, status, remaining_use_count AS remainingUseCount
            FROM user_account
            WHERE phone = #{phone}
              AND role = #{role}
            LIMIT 1
            """)
    UserEntity findByPhoneAndRole(@Param("phone") String phone, @Param("role") Integer role);

    @Select("""
            SELECT id, name, phone, password, role, status, remaining_use_count AS remainingUseCount
            FROM user_account
            WHERE id = #{id}
            LIMIT 1
            """)
    UserEntity findById(@Param("id") Long id);

    @Insert("""
            INSERT INTO user_account(name, phone, password, role, status)
            VALUES(#{name}, #{phone}, #{password}, #{role}, #{status})
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(UserEntity user);

    @Select("""
            SELECT id, name, phone, password, role, status, remaining_use_count AS remainingUseCount
            FROM user_account
            WHERE id = #{merchantId}
              AND role = 3
            LIMIT 1
            """)
    UserEntity findMerchantById(@Param("merchantId") Long merchantId);

    @Select("""
            SELECT id, name, phone, password, role, status, remaining_use_count AS remainingUseCount
            FROM user_account
            WHERE id = #{teacherId}
              AND role = 2
            LIMIT 1
            """)
    UserEntity findTeacherById(@Param("teacherId") Long teacherId);

    @Select("""
            SELECT id, name, phone, password, role, status, remaining_use_count AS remainingUseCount
            FROM user_account
            WHERE id = #{developerId}
              AND role = 4
            LIMIT 1
            """)
    UserEntity findDeveloperById(@Param("developerId") Long developerId);

    @Select("""
            <script>
            SELECT id, name, phone, password, role, status, remaining_use_count AS remainingUseCount
            FROM user_account
            WHERE role = 3
              <if test="keyword != null and keyword != ''">
                AND name LIKE CONCAT('%', #{keyword}, '%')
              </if>
            ORDER BY id DESC
            LIMIT 100
            </script>
            """)
    List<UserEntity> searchMerchants(@Param("keyword") String keyword);

    @Select("""
            <script>
            SELECT DISTINCT ua.id, ua.name, ua.phone, ua.password, ua.role, ua.status, ua.remaining_use_count AS remainingUseCount
            FROM user_account ua
            INNER JOIN order_record o ON o.merchant_id = ua.id
            WHERE ua.role = 3
              AND o.user_id = #{userId}
              <if test="keyword != null and keyword != ''">
                AND ua.name LIKE CONCAT('%', #{keyword}, '%')
              </if>
            ORDER BY ua.id DESC
            LIMIT 100
            </script>
            """)
    List<UserEntity> searchMerchantsByUserOrders(@Param("userId") Long userId, @Param("keyword") String keyword);

    @Update("""
            UPDATE user_account
            SET remaining_use_count = remaining_use_count - 1
            WHERE id = #{merchantId}
              AND role = 3
              AND remaining_use_count > 0
            """)
    int decrementMerchantRemainingUseCount(@Param("merchantId") Long merchantId);

    @Update("""
            UPDATE user_account
            SET name = #{name},
                phone = #{phone},
                password = #{password},
                role = #{role},
                status = #{status},
                remaining_use_count = #{remainingUseCount}
            WHERE id = #{id}
            """)
    int updateById(UserEntity user);

    @Update("""
            UPDATE user_account
            SET status = 0
            WHERE id = #{id}
            """)
    int disableById(@Param("id") Long id);

    @Update("""
            UPDATE user_account
            SET status = 1
            WHERE id = #{id}
            """)
    int enableById(@Param("id") Long id);

    @Select("""
            <script>
            SELECT id, name, phone, password, role, status, remaining_use_count AS remainingUseCount
            FROM user_account
            <where>
              <if test="role != null">
                role = #{role}
              </if>
              <if test="keyword != null and keyword != ''">
                AND (phone LIKE CONCAT('%', #{keyword}, '%') OR name LIKE CONCAT('%', #{keyword}, '%'))
              </if>
            </where>
            ORDER BY id DESC
            LIMIT #{offset}, #{pageSize}
            </script>
            """)
    List<UserEntity> listByRole(@Param("role") Integer role,
                                @Param("keyword") String keyword,
                                @Param("offset") int offset,
                                @Param("pageSize") int pageSize);

    @Select("""
            <script>
            SELECT COUNT(1)
            FROM user_account
            <where>
              <if test="role != null">
                role = #{role}
              </if>
              <if test="keyword != null and keyword != ''">
                AND (phone LIKE CONCAT('%', #{keyword}, '%') OR name LIKE CONCAT('%', #{keyword}, '%'))
              </if>
            </where>
            </script>
            """)
    long countByRole(@Param("role") Integer role, @Param("keyword") String keyword);
}
