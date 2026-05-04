package com.example.springbootdemo.hardware.controller;

import com.example.springbootdemo.hardware.HardwareControlRequest;
import com.example.springbootdemo.hardware.HardwareControlResponse;
import com.example.springbootdemo.hardware.ProjectCategory;
import com.example.springbootdemo.hardware.ProjectItem;
import com.example.springbootdemo.hardware.SendByOrderRequest;
import com.example.springbootdemo.hardware.SendByProjectRequest;
import com.example.springbootdemo.auth.dao.UserEntity;
import com.example.springbootdemo.auth.dao.UserMapper;
import com.example.springbootdemo.device.dao.DeviceEntity;
import com.example.springbootdemo.device.dao.DeviceMapper;
import com.example.springbootdemo.order.dao.OrderEntity;
import com.example.springbootdemo.order.dao.OrderMapper;
import com.example.springbootdemo.hardware.service.DeviceMqttService;
import com.example.springbootdemo.hardware.service.HardwareProtocolService;
import com.example.springbootdemo.hardware.service.ProjectCatalogService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

@RestController
@RequestMapping("/api/hardware")
public class HardwareControlController {

    private static final Logger log = LoggerFactory.getLogger(HardwareControlController.class);

    private final HardwareProtocolService protocolService;
    private final DeviceMqttService mqttService;
    private final ProjectCatalogService projectCatalogService;
    private final DeviceMapper deviceMapper;
    private final UserMapper userMapper;
    private final OrderMapper orderMapper;

    public HardwareControlController(HardwareProtocolService protocolService,
                                     DeviceMqttService mqttService,
                                     ProjectCatalogService projectCatalogService,
                                     DeviceMapper deviceMapper,
                                     UserMapper userMapper,
                                     OrderMapper orderMapper) {
        this.protocolService = protocolService;
        this.mqttService = mqttService;
        this.projectCatalogService = projectCatalogService;
        this.deviceMapper = deviceMapper;
        this.userMapper = userMapper;
        this.orderMapper = orderMapper;
    }

    @GetMapping("/commands")
    public HardwareControlResponse commands() {
        log.info("调用接口 /api/hardware/commands");
        return new HardwareControlResponse(
                mqttService.serverCommand(),
                mqttService.subscribeCommand(),
                mqttService.publishCommand(),
                null,
                null,
                null
        );
    }

    @GetMapping("/projects")
    public List<ProjectCategory> projects() {
        List<ProjectCategory> categories = projectCatalogService.getProjectCategories();
        log.info("调用接口 /api/hardware/projects, 分类数={}", categories.size());
        return categories;
    }

    @GetMapping("/projects-flat")
    public List<ProjectItem> projectsFlat() {
        List<ProjectItem> projects = projectCatalogService.getProjects();
        log.info("调用接口 /api/hardware/projects-flat, 项目数={}", projects.size());
        return projects;
    }

    @PostMapping("/frame")
    public HardwareControlResponse buildFrame(@Valid @RequestBody HardwareControlRequest request) {
        log.info("调用接口 /api/hardware/frame, customerId={}, projectCode={}",
                request.getCustomerId(), request.getProjectCode());
        byte[] frame = protocolService.buildFrame(request);
        return new HardwareControlResponse(
                mqttService.serverCommand(),
                mqttService.subscribeCommand(),
                mqttService.publishCommand(),
                protocolService.toHex(frame),
                null,
                null
        );
    }

    @PostMapping("/send")
    public HardwareControlResponse send(@Valid @RequestBody HardwareControlRequest request) {
        int machineNo = request.getMachineNo() != null ? request.getMachineNo() : 0;
        log.info("调用接口 /api/hardware/send, customerId={}, projectCode={}, machineNo={}",
                request.getCustomerId(), request.getProjectCode(), machineNo);
        byte[] frame = protocolService.buildFrame(request);
        mqttService.publish(frame, machineNo);
        log.info("发送完成 /api/hardware/send, customerId={}", request.getCustomerId());
        return new HardwareControlResponse(
                mqttService.serverCommand(),
                mqttService.subscribeCommand(),
                mqttService.publishCommand(),
                protocolService.toHex(frame),
                null,
                null
        );
    }

    @PostMapping("/send-by-project")
    @Transactional
    public HardwareControlResponse sendByProject(@Valid @RequestBody SendByProjectRequest request) {
        int machineNo = request.getMachineNo() != null ? request.getMachineNo() : 0;
        log.info("调用接口 /api/hardware/send-by-project, customerId={}, projectName={}, machineNo={}",
                request.getCustomerId(), request.getProjectName(), machineNo);
        ProjectItem project = projectCatalogService.findByProjectName(request.getProjectName())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "未知项目名称: " + request.getProjectName()));

        String machineNo4 = String.format(Locale.ROOT, "%04d", machineNo);
        DeviceEntity device = deviceMapper.findByMachineNo(machineNo4);
        if (device == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "设备不存在: machineNo=" + machineNo4);
        }
        if (device.getStatus() != null && device.getStatus() == 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "设备已停用");
        }

        UserEntity merchant = userMapper.findMerchantById(device.getMerchantId());
        if (merchant == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "设备绑定的商家不存在");
        }
        boolean inFreeWindow = device.getFreeUseDeadline() != null && !LocalDateTime.now().isAfter(device.getFreeUseDeadline());
        if (!inFreeWindow) {
            if (merchant.getRemainingUseCount() == null || merchant.getRemainingUseCount() <= 0) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "商家剩余次数不足");
            }
        }

        UserEntity user = userMapper.findByPhone(request.getCustomerId());
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "用户不存在，请先登录");
        }

        HardwareControlRequest control = SendByProjectRequest.toControlRequest(request, project.code());
        byte[] frame = protocolService.buildFrame(control);
        mqttService.publish(frame, machineNo);

        OrderEntity order = new OrderEntity();
        order.setUserId(user.getId());
        order.setMerchantId(device.getMerchantId());
        order.setDeviceId(device.getId());
        order.setGender(control.getGender());
        order.setAge(control.getAge());
        order.setHeight(control.getHeight());
        order.setWeight(control.getWeight());
        order.setSportPerformance(control.getSportPerformance());
        order.setProjectName(project.name());
        order.setProjectDuration(control.getProjectMinutes());
        order.setUsageCount(1);
        orderMapper.insertUsage(order);

        if (!inFreeWindow) {
            int affected = userMapper.decrementMerchantRemainingUseCount(device.getMerchantId());
            if (affected == 0) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "商家剩余次数不足");
            }
        }

        log.info("发送完成 /api/hardware/send-by-project, projectCode={}, projectName={}",
                project.code(), project.name());
        return new HardwareControlResponse(
                mqttService.serverCommand(),
                mqttService.subscribeCommand(),
                mqttService.publishCommand(),
                protocolService.toHex(frame),
                project.code(),
                project.name()
        );
    }

    @PostMapping("/send-by-order")
    @Transactional
    public HardwareControlResponse sendByOrder(@Valid @RequestBody SendByOrderRequest request) {
        log.info("调用接口 /api/hardware/send-by-order, orderId={}, machineNo={}", request.getOrderId(), request.getMachineNo());
        OrderEntity order = orderMapper.findById(request.getOrderId());
        if (order == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "订单不存在");
        }
        if (order.getUsageCount() == null || order.getUsageCount() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "订单剩余使用次数不足");
        }
        if (order.getProjectName() == null || order.getProjectName().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "订单项目名称为空");
        }
        ProjectItem project = projectCatalogService.findByProjectName(order.getProjectName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "订单项目名称无效: " + order.getProjectName()));

        UserEntity user = userMapper.findById(order.getUserId());
        if (user == null || user.getPhone() == null || user.getPhone().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "订单关联用户不存在");
        }

        DeviceEntity device = null;
        if (request.getMachineNo() == null) {
            if (order.getDeviceId() == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "请传machineNo，订单未绑定设备");
            }
            device = deviceMapper.findById(order.getDeviceId());
            if (device == null || device.getMachineNo() == null || device.getMachineNo().isBlank()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "订单绑定设备不存在");
            }
        }

        int machineNo = request.getMachineNo() != null ? request.getMachineNo() : Integer.parseInt(device.getMachineNo());
        String machineNo4 = String.format(Locale.ROOT, "%04d", machineNo);
        DeviceEntity targetDevice = deviceMapper.findByMachineNo(machineNo4);
        if (targetDevice == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "设备不存在: machineNo=" + machineNo4);
        }
        if (targetDevice.getStatus() != null && targetDevice.getStatus() == 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "设备已停用");
        }

        UserEntity merchant = userMapper.findMerchantById(targetDevice.getMerchantId());
        if (merchant == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "设备绑定的商家不存在");
        }
        boolean inFreeWindow = targetDevice.getFreeUseDeadline() != null && !LocalDateTime.now().isAfter(targetDevice.getFreeUseDeadline());
        if (!inFreeWindow && (merchant.getRemainingUseCount() == null || merchant.getRemainingUseCount() <= 0)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "商家剩余次数不足");
        }

        HardwareControlRequest control = new HardwareControlRequest();
        control.setCustomerId(user.getPhone());
        control.setGender(order.getGender() == null ? 0 : order.getGender());
        control.setHeight(order.getHeight() == null ? 170 : order.getHeight());
        control.setAge(order.getAge() == null ? 28 : order.getAge());
        control.setWeight(order.getWeight() == null ? 60 : order.getWeight());
        control.setProjectCode(project.code());
        control.setProjectMinutes(order.getProjectDuration() == null ? 40 : order.getProjectDuration());
        control.setSportPerformance(order.getSportPerformance() == null ? 0 : order.getSportPerformance());
        control.setUsageCount(order.getUsageCount() == null ? 1 : order.getUsageCount());
        control.setMachineNo(machineNo);

        byte[] frame = protocolService.buildFrame(control);
        mqttService.publish(frame, machineNo);

        OrderEntity usage = new OrderEntity();
        usage.setUserId(user.getId());
        usage.setMerchantId(targetDevice.getMerchantId());
        usage.setDeviceId(targetDevice.getId());
        usage.setGender(control.getGender());
        usage.setAge(control.getAge());
        usage.setHeight(control.getHeight());
        usage.setWeight(control.getWeight());
        usage.setSportPerformance(control.getSportPerformance());
        usage.setProjectName(project.name());
        usage.setProjectDuration(control.getProjectMinutes());
        usage.setUsageCount(1);
        orderMapper.insertUsage(usage);

        int orderAffected = orderMapper.decrementOrderUsageCount(order.getId());
        if (orderAffected == 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "订单剩余使用次数不足");
        }

        if (!inFreeWindow) {
            int affected = userMapper.decrementMerchantRemainingUseCount(targetDevice.getMerchantId());
            if (affected == 0) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "商家剩余次数不足");
            }
        }

        return new HardwareControlResponse(
                mqttService.serverCommand(),
                mqttService.subscribeCommand(),
                mqttService.publishCommand(),
                protocolService.toHex(frame),
                project.code(),
                project.name()
        );
    }
}
