package com.example.springbootdemo.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "device.mqtt")
public class DeviceMqttProperties {

    private String ip;
    private int port;
    private String username;
    private String password;
    private String subscribeTopic;
    private String publishTopicPrefix;

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSubscribeTopic() {
        return subscribeTopic;
    }

    public void setSubscribeTopic(String subscribeTopic) {
        this.subscribeTopic = subscribeTopic;
    }

    public String getPublishTopicPrefix() {
        return publishTopicPrefix;
    }

    public void setPublishTopicPrefix(String publishTopicPrefix) {
        this.publishTopicPrefix = publishTopicPrefix;
    }
}
