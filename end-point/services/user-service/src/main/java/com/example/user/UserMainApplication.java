package com.example.user;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@MapperScan("com.example.user.mapper")
@ComponentScan(basePackages = {
        "com.example.user",    // 当前服务
        "com.example.common", // 公共工具（JwtUtil、R、UserDTO）
        "com.example.config"  // 框架配置（RedisConfig、MyBatisPlusConfig）
})
public class UserMainApplication {
    public static void main(String[] args) {
        SpringApplication.run(UserMainApplication.class, args);
    }
}
