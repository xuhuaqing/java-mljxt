package com.example.springbootdemo.hardware.service;

import com.example.springbootdemo.config.DeviceMqttProperties;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
@DependsOn("embeddedMqttBrokerService")
public class DeviceMqttService {

    private static final Logger log = LoggerFactory.getLogger(DeviceMqttService.class);

    private final DeviceMqttProperties properties;
    private MqttClient mqttClient;

    public DeviceMqttService(DeviceMqttProperties properties) {
        this.properties = properties;
    }

    @PostConstruct
    public void init() {
        try {
            this.mqttClient = new MqttClient(serverUri(), MqttClient.generateClientId(), new MemoryPersistence());
            this.mqttClient.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable cause) {
                    log.warn("MQTT连接断开", cause);
                }

                @Override
                public void messageArrived(String topic, MqttMessage message) {
                    log.info("收到MQTT消息 topic={}, payload={}", topic, bytesToHex(message.getPayload()));
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {
                    log.debug("MQTT消息投递完成: {}", token.isComplete());
                }
            });
            connectIfNeeded();
            this.mqttClient.subscribe(properties.getSubscribeTopic());
            log.info("MQTT已订阅主题: {}", properties.getSubscribeTopic());
        } catch (MqttException ex) {
            throw new IllegalStateException("MQTT初始化失败: " + ex.getMessage(), ex);
        }
    }

    public void publish(byte[] payload) {
        publish(payload, 0);
    }

    public void publish(byte[] payload, int machineNo) {
        try {
            connectIfNeeded();
            String topic = buildPublishTopic(machineNo);
            MqttMessage message = new MqttMessage(payload);
            message.setQos(1);
            this.mqttClient.publish(topic, message);
            log.info("已发送MQTT消息 topic={}, bytes={}", topic, payload.length);
        } catch (MqttException ex) {
            throw new IllegalStateException("MQTT发布失败: " + ex.getMessage(), ex);
        }
    }

    @PreDestroy
    public void destroy() {
        if (this.mqttClient != null) {
            try {
                this.mqttClient.disconnect();
                this.mqttClient.close();
            } catch (MqttException ex) {
                log.warn("MQTT关闭失败", ex);
            }
        }
    }

    public String serverCommand() {
        return "AT+SETSTDMQTT:IP=%s,PORT=%d,username=%s,password=%s"
                .formatted(properties.getIp(), properties.getPort(), properties.getUsername(), properties.getPassword());
    }

    public String subscribeCommand() {
        return "AT+mqttSubscribeset:Subscribe=%s".formatted(properties.getSubscribeTopic());
    }

    public String publishCommand() {
        return "AT+mqttPublishset:Publish=%s{0000-9999}".formatted(properties.getPublishTopicPrefix());
    }

    public String buildPublishTopic(int machineNo) {
        if (machineNo < 0 || machineNo > 9999) {
            throw new IllegalArgumentException("机器编号必须在0到9999之间");
        }
        return "%s%s".formatted(properties.getPublishTopicPrefix(), String.format(Locale.ROOT, "%04d", machineNo));
    }

    private void connectIfNeeded() throws MqttException {
        if (this.mqttClient == null || this.mqttClient.isConnected()) {
            return;
        }
        MqttConnectOptions options = new MqttConnectOptions();
        options.setUserName(properties.getUsername());
        options.setPassword(properties.getPassword().toCharArray());
        options.setAutomaticReconnect(true);
        options.setCleanSession(true);
        this.mqttClient.connect(options);
        log.info("MQTT连接成功: {}", serverUri());
    }

    private String serverUri() {
        return "tcp://%s:%d".formatted(properties.getIp(), properties.getPort());
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder builder = new StringBuilder(bytes.length * 3);
        for (byte value : bytes) {
            builder.append(String.format("%02X ", value));
        }
        return builder.toString().trim();
    }
}
