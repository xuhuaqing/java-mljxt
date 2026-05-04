package com.example.springbootdemo.auth.controller;

import com.example.springbootdemo.auth.AdminMeVO;
import com.example.springbootdemo.auth.AdminUserVO;
import com.example.springbootdemo.auth.AdminUserUpsertRequest;
import com.example.springbootdemo.auth.service.AuthService;
import com.example.springbootdemo.common.ApiResponse;
import com.example.springbootdemo.device.AdminDeviceUpsertRequest;
import com.example.springbootdemo.device.AdminDeviceVO;
import com.example.springbootdemo.device.UpdateDeviceDeadlineRequest;
import com.example.springbootdemo.device.service.DeviceService;
import com.example.springbootdemo.order.AdminDeviceUsageRecordVO;
import com.example.springbootdemo.order.service.OrderService;
import com.example.springbootdemo.security.AuthContext;
import com.example.springbootdemo.security.CurrentUser;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.nio.charset.StandardCharsets;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private static final Logger log = LoggerFactory.getLogger(AdminController.class);

    private final AuthService authService;
    private final DeviceService deviceService;
    private final OrderService orderService;

    public AdminController(AuthService authService, DeviceService deviceService, OrderService orderService) {
        this.authService = authService;
        this.deviceService = deviceService;
        this.orderService = orderService;
    }

    @GetMapping("/me")
    public ApiResponse<AdminMeVO> me() {
        CurrentUser currentUser = AuthContext.get();
        log.info("调用接口 /api/admin/me, userId={}", currentUser != null ? currentUser.userId() : null);
        requireAdmin(currentUser);
        return ApiResponse.success(authService.me(currentUser.userId()));
    }

    @GetMapping("/menus")
    public ApiResponse<List<Map<String, Object>>> menus() {
        CurrentUser currentUser = AuthContext.get();
        requireAdmin(currentUser);
        List<Map<String, Object>> menus = List.of(
                Map.of("path", "/dashboard", "name", "首页"),
                Map.of("path", "/users", "name", "用户列表"),
                Map.of("path", "/devices", "name", "设备管理"),
                Map.of("path", "/device-usage-records", "name", "设备使用记录"),
                Map.of("path", "/developer-merchant-bind", "name", "开发绑定商家")
        );
        return ApiResponse.success(menus);
    }

    @GetMapping("/users")
    public ApiResponse<Map<String, Object>> users(
            @RequestParam(required = false) Integer role,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") Integer pageNo,
            @RequestParam(defaultValue = "20") Integer pageSize
    ) {
        requireAdmin(AuthContext.get());
        if (pageNo < 1 || pageSize < 1 || pageSize > 100) {
            throw new IllegalArgumentException("分页参数不合法");
        }
        List<AdminUserVO> records = authService.listUsers(role, keyword, pageNo, pageSize);
        long total = authService.countUsers(role, keyword);
        return ApiResponse.success(Map.of(
                "total", total,
                "pageNo", pageNo,
                "pageSize", pageSize,
                "records", records
        ));
    }

    @PostMapping("/users")
    public ApiResponse<AdminUserVO> createUser(@Valid @RequestBody AdminUserUpsertRequest request) {
        requireAdmin(AuthContext.get());
        return ApiResponse.success(authService.createUser(request));
    }

    @PutMapping("/users/{id}")
    public ApiResponse<AdminUserVO> updateUser(@PathVariable Long id, @Valid @RequestBody AdminUserUpsertRequest request) {
        requireAdmin(AuthContext.get());
        return ApiResponse.success(authService.updateUser(id, request));
    }

    @PutMapping("/users/{id}/disable")
    public ApiResponse<Void> disableUser(@PathVariable Long id) {
        requireAdmin(AuthContext.get());
        authService.deleteUser(id);
        return ApiResponse.success(null);
    }

    @PutMapping("/users/{id}/enable")
    public ApiResponse<Void> enableUser(@PathVariable Long id) {
        requireAdmin(AuthContext.get());
        authService.enableUser(id);
        return ApiResponse.success(null);
    }

    @GetMapping("/devices")
    public ApiResponse<Map<String, Object>> devices(
            @RequestParam(required = false) Long merchantId,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") Integer pageNo,
            @RequestParam(defaultValue = "20") Integer pageSize
    ) {
        requireAdmin(AuthContext.get());
        if (pageNo < 1 || pageSize < 1 || pageSize > 100) {
            throw new IllegalArgumentException("分页参数不合法");
        }
        List<AdminDeviceVO> records = deviceService.listForAdmin(merchantId, keyword, pageNo, pageSize);
        long total = deviceService.countForAdmin(merchantId, keyword);
        return ApiResponse.success(Map.of(
                "total", total,
                "pageNo", pageNo,
                "pageSize", pageSize,
                "records", records
        ));
    }

    @PostMapping("/devices")
    public ApiResponse<AdminDeviceVO> createDevice(@Valid @RequestBody AdminDeviceUpsertRequest request) {
        requireAdmin(AuthContext.get());
        return ApiResponse.success(deviceService.createDevice(request));
    }

    @PutMapping("/devices/{id}/free-use-deadline")
    public ApiResponse<Void> updateDeadline(@PathVariable Long id, @Valid @RequestBody UpdateDeviceDeadlineRequest request) {
        requireAdmin(AuthContext.get());
        deviceService.updateFreeUseDeadline(id, request.getFreeUseDeadline());
        return ApiResponse.success(null);
    }

    @PutMapping("/devices/{id}/disable")
    public ApiResponse<Void> disableDevice(@PathVariable Long id) {
        requireAdmin(AuthContext.get());
        deviceService.disableDevice(id);
        return ApiResponse.success(null);
    }

    @PutMapping("/devices/{id}/enable")
    public ApiResponse<Void> enableDevice(@PathVariable Long id) {
        requireAdmin(AuthContext.get());
        deviceService.enableDevice(id);
        return ApiResponse.success(null);
    }

    @GetMapping("/device-usage-records")
    public ApiResponse<Map<String, Object>> deviceUsageRecords(
            @RequestParam(required = false) Long merchantId,
            @RequestParam(required = false) Long deviceId,
            @RequestParam(defaultValue = "1") Integer pageNo,
            @RequestParam(defaultValue = "20") Integer pageSize
    ) {
        requireAdmin(AuthContext.get());
        if (pageNo < 1 || pageSize < 1 || pageSize > 200) {
            throw new IllegalArgumentException("分页参数不合法");
        }
        List<AdminDeviceUsageRecordVO> records = orderService.queryAdminDeviceUsageRecords(merchantId, deviceId, pageNo, pageSize);
        long total = orderService.countAdminDeviceUsageRecords(merchantId, deviceId);
        return ApiResponse.success(Map.of(
                "total", total,
                "pageNo", pageNo,
                "pageSize", pageSize,
                "records", records
        ));
    }

    @GetMapping("/device-usage-records/export")
    public ResponseEntity<byte[]> exportDeviceUsageRecords(
            @RequestParam(required = false) Long merchantId,
            @RequestParam(required = false) Long deviceId
    ) {
        requireAdmin(AuthContext.get());
        List<AdminDeviceUsageRecordVO> rows = orderService.queryAdminDeviceUsageRecordsForExport(merchantId, deviceId);
        StringBuilder sb = new StringBuilder();
        sb.append("使用记录ID\t商家ID\t商家名称\t设备ID\t设备名称\t用户ID\t用户手机号\t项目名称\t使用次数\t创建时间\n");
        for (AdminDeviceUsageRecordVO r : rows) {
            sb.append(n(r.orderId())).append('\t')
                    .append(n(r.merchantId())).append('\t')
                    .append(n(r.merchantName())).append('\t')
                    .append(n(r.deviceId())).append('\t')
                    .append(n(r.deviceName())).append('\t')
                    .append(n(r.userId())).append('\t')
                    .append(n(r.userPhone())).append('\t')
                    .append(n(r.projectName())).append('\t')
                    .append(n(r.usageCount())).append('\t')
                    .append(n(r.createdAt())).append('\n');
        }
        byte[] bytes = sb.toString().getBytes(StandardCharsets.UTF_8);
        String fileName = URLEncoder.encode("device-usage-records.xls", StandardCharsets.UTF_8);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''" + fileName)
                .contentType(MediaType.parseMediaType("application/vnd.ms-excel; charset=UTF-8"))
                .body(bytes);
    }

    private String n(Object v) {
        return v == null ? "" : v.toString();
    }

    private void requireAdmin(CurrentUser currentUser) {
        if (currentUser == null) {
            throw new IllegalArgumentException("用户未登录");
        }
        if (currentUser.role() == null || currentUser.role() != 0) {
            throw new IllegalArgumentException("仅后台管理员可访问");
        }
    }
}
