package com.example.springbootdemo.hardware;

public record HardwareControlResponse(
        String mqttServerCommand,
        String mqttSubscribeCommand,
        String mqttPublishCommand,
        String payloadHex,
        Integer resolvedProjectCode,
        String resolvedProjectName
) {
}
