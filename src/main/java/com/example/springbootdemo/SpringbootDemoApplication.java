package com.example.springbootdemo;

import com.example.springbootdemo.config.DeviceMqttProperties;
import com.example.springbootdemo.config.EmbeddedMqttProperties;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({DeviceMqttProperties.class, EmbeddedMqttProperties.class})
@MapperScan({
        "com.example.springbootdemo.auth.dao",
        "com.example.springbootdemo.order.dao",
        "com.example.springbootdemo.device.dao"
})
public class SpringbootDemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringbootDemoApplication.class, args);
	}

}
