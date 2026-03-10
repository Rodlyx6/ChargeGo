package com.example.order;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

@EnableFeignClients
@SpringBootApplication
@MapperScan("com.example.order.mapper")
@ComponentScan(basePackages = {
        "com.example.order",   // 当前服务
        "com.example.common",  // 公共工具（JwtUtil、R、UserDTO）
        "com.example.config"   // 框架配置（RedisConfig、MyBatisPlusConfig）
})
public class OrderMainApplication {
    public static void main(String[] args) {
        SpringApplication.run(OrderMainApplication.class, args);
    }
}
