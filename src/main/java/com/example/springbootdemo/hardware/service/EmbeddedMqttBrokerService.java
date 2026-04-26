package com.example.springbootdemo.hardware.service;

import com.example.springbootdemo.config.EmbeddedMqttProperties;
import io.moquette.broker.Server;
import io.moquette.broker.config.IConfig;
import io.moquette.broker.config.MemoryConfig;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Properties;

@Service
public class EmbeddedMqttBrokerService {

    private static final Logger log = LoggerFactory.getLogger(EmbeddedMqttBrokerService.class);

    private final EmbeddedMqttProperties properties;
    private Server broker;

    public EmbeddedMqttBrokerService(EmbeddedMqttProperties properties) {
        this.properties = properties;
    }

    @PostConstruct
    public void start() {
        if (!properties.isEnabled()) {
            log.info("内嵌MQTT Broker已关闭 (embedded.mqtt.enabled=false)");
            return;
        }
        try {
            this.broker = new Server();
            Properties config = new Properties();
            config.setProperty(IConfig.HOST_PROPERTY_NAME, properties.getHost());
            config.setProperty(IConfig.PORT_PROPERTY_NAME, String.valueOf(properties.getPort()));
            config.setProperty(IConfig.ALLOW_ANONYMOUS_PROPERTY_NAME, String.valueOf(properties.isAllowAnonymous()));
            this.broker.startServer(new MemoryConfig(config));
            log.info("内嵌MQTT Broker启动成功: {}:{}", properties.getHost(), properties.getPort());
        } catch (IOException ex) {
            throw new IllegalStateException("内嵌MQTT Broker启动失败: " + ex.getMessage(), ex);
        }
    }

    @PreDestroy
    public void stop() {
        if (this.broker != null) {
            this.broker.stopServer();
            log.info("内嵌MQTT Broker已停止");
        }
    }
}
