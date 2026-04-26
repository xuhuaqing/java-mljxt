package com.example.springbootdemo.hardware.controller;

import com.example.springbootdemo.hardware.HardwareControlRequest;
import com.example.springbootdemo.hardware.HardwareControlResponse;
import com.example.springbootdemo.hardware.ProjectCategory;
import com.example.springbootdemo.hardware.ProjectItem;
import com.example.springbootdemo.hardware.SendByProjectRequest;
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

import java.util.List;

@RestController
@RequestMapping("/api/hardware")
public class HardwareControlController {

    private static final Logger log = LoggerFactory.getLogger(HardwareControlController.class);

    private final HardwareProtocolService protocolService;
    private final DeviceMqttService mqttService;
    private final ProjectCatalogService projectCatalogService;

    public HardwareControlController(HardwareProtocolService protocolService,
                                     DeviceMqttService mqttService,
                                     ProjectCatalogService projectCatalogService) {
        this.protocolService = protocolService;
        this.mqttService = mqttService;
        this.projectCatalogService = projectCatalogService;
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
    public HardwareControlResponse sendByProject(@Valid @RequestBody SendByProjectRequest request) {
        int machineNo = request.getMachineNo() != null ? request.getMachineNo() : 0;
        log.info("调用接口 /api/hardware/send-by-project, customerId={}, projectName={}, machineNo={}",
                request.getCustomerId(), request.getProjectName(), machineNo);
        ProjectItem project = projectCatalogService.findByProjectName(request.getProjectName())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "未知项目名称: " + request.getProjectName()));
        HardwareControlRequest control = SendByProjectRequest.toControlRequest(request, project.code());
        byte[] frame = protocolService.buildFrame(control);
        mqttService.publish(frame, machineNo);
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
}
