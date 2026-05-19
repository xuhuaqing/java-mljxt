package com.example.springbootdemo.auth.service;

import com.example.springbootdemo.auth.LoginRequest;
import com.example.springbootdemo.auth.LoginUserVO;
import com.example.springbootdemo.auth.AdminMeVO;
import com.example.springbootdemo.auth.AdminUserVO;
import com.example.springbootdemo.auth.AdminUserUpsertRequest;
import com.example.springbootdemo.auth.UserRole;
import com.example.springbootdemo.auth.dao.UserEntity;
import com.example.springbootdemo.auth.dao.UserMapper;
import com.example.springbootdemo.security.TokenService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserMapper userMapper;
    private final TokenService tokenService;
    private final String adminAccount;
    private final String adminPassword;

    public AuthServiceImpl(
            UserMapper userMapper,
            TokenService tokenService,
            @Value("${app.admin.account:admin}") String adminAccount,
            @Value("${app.admin.password:admin123}") String adminPassword
    ) {
        this.userMapper = userMapper;
        this.tokenService = tokenService;
        this.adminAccount = adminAccount;
        this.adminPassword = adminPassword;
    }

    @Override
    public LoginUserVO login(LoginRequest request) {
        if (adminAccount.equals(request.getAccount())) {
            if (!adminPassword.equals(request.getPassword())) {
                throw new IllegalArgumentException("密码错误");
            }
            return new LoginUserVO(
                    0L,
                    adminAccount,
                    0,
                    "管理员",
                    null,
                    tokenService.issueToken(0L, 0)
            );
        }

        if (request.getRole() == null) {
            throw new IllegalArgumentException("普通账号登录必须传role");
        }
        UserEntity user = userMapper.findByPhoneAndRole(request.getAccount(), request.getRole());
        if (user == null) {
            throw new IllegalArgumentException("手机号或身份不存在");
        }
        if (!Objects.equals(user.getStatus(), 1)) {
            throw new IllegalArgumentException("账号已停用，无法登录");
        }
        if (!request.getPassword().equals(user.getPassword())) {
            throw new IllegalArgumentException("密码错误");
        }
        return new LoginUserVO(
                user.getId(),
                user.getPhone(),
                user.getRole(),
                UserRole.labelOf(user.getRole()),
                user.getRole() != null && user.getRole() == 3 ? user.getRemainingUseCount() : null,
                tokenService.issueToken(user.getId(), user.getRole())
        );
    }

    @Override
    public AdminMeVO me(Long userId) {
        if (userId != null && userId == 0L) {
            return new AdminMeVO(0L, adminAccount, 0, "管理员", null);
        }
        throw new IllegalArgumentException("用户不存在");
    }

    @Override
    public List<AdminUserVO> listUsers(Integer role, String keyword, int pageNo, int pageSize) {
        if (role != null && (role < 1 || role > 4)) {
            throw new IllegalArgumentException("角色只能是1-4");
        }
        int offset = (pageNo - 1) * pageSize;
        List<UserEntity> rows = userMapper.listByRole(role, keyword, offset, pageSize);
        return rows.stream().map(u -> new AdminUserVO(
                u.getId(),
                u.getName(),
                u.getPhone(),
                u.getPassword(),
                u.getRole(),
                UserRole.labelOf(u.getRole()),
                u.getStatus(),
                u.getRole() != null && u.getRole() == 3 ? u.getRemainingUseCount() : null
        )).toList();
    }

    @Override
    public long countUsers(Integer role, String keyword) {
        return userMapper.countByRole(role, keyword);
    }

    @Override
    public AdminUserVO createUser(AdminUserUpsertRequest request) {
        UserEntity exists = userMapper.findByPhoneAndRole(request.getPhone(), request.getRole());
        if (exists != null) {
            throw new IllegalArgumentException("该手机号在此身份下已存在");
        }
        UserEntity user = new UserEntity();
        user.setName(request.getName());
        user.setPhone(request.getPhone());
        user.setPassword(request.getPassword());
        user.setRole(request.getRole());
        user.setStatus(1);
        user.setRemainingUseCount(request.getRole() == 3
                ? Math.max(request.getRemainingUseCount() == null ? 0 : request.getRemainingUseCount(), 0)
                : 0);
        userMapper.insert(user);
        return toAdminUserVO(userMapper.findById(user.getId()));
    }

    @Override
    public AdminUserVO updateUser(Long id, AdminUserUpsertRequest request) {
        UserEntity current = userMapper.findById(id);
        if (current == null || current.getRole() == null || current.getRole() < 1 || current.getRole() > 4) {
            throw new IllegalArgumentException("用户不存在");
        }
        UserEntity exists = userMapper.findByPhoneAndRole(request.getPhone(), request.getRole());
        if (exists != null && !exists.getId().equals(id)) {
            throw new IllegalArgumentException("该手机号在此身份下已存在");
        }
        current.setName(request.getName());
        current.setPhone(request.getPhone());
        current.setPassword(request.getPassword());
        current.setRole(request.getRole());
        current.setStatus(current.getStatus() == null ? 1 : current.getStatus());
        current.setRemainingUseCount(request.getRole() == 3
                ? Math.max(request.getRemainingUseCount() == null ? 0 : request.getRemainingUseCount(), 0)
                : 0);
        userMapper.updateById(current);
        return toAdminUserVO(userMapper.findById(id));
    }

    @Override
    public void deleteUser(Long id) {
        UserEntity current = userMapper.findById(id);
        if (current == null || current.getRole() == null || current.getRole() < 1 || current.getRole() > 4) {
            throw new IllegalArgumentException("用户不存在");
        }
        userMapper.disableById(id);
    }

    @Override
    public void enableUser(Long id) {
        UserEntity current = userMapper.findById(id);
        if (current == null || current.getRole() == null || current.getRole() < 1 || current.getRole() > 4) {
            throw new IllegalArgumentException("用户不存在");
        }
        userMapper.enableById(id);
    }

    private AdminUserVO toAdminUserVO(UserEntity u) {
        return new AdminUserVO(
                u.getId(),
                u.getName(),
                u.getPhone(),
                u.getPassword(),
                u.getRole(),
                UserRole.labelOf(u.getRole()),
                u.getStatus(),
                u.getRole() != null && u.getRole() == 3 ? u.getRemainingUseCount() : null
        );
    }
}
