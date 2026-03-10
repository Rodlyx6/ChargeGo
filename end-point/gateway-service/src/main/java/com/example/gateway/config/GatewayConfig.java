package com.example.gateway.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.AntPathMatcher;

/**
 * Gateway 通用配置
 */
@Configuration
public class GatewayConfig {

    /**
     * Jackson ObjectMapper：支持 Java 8 时间类型
     */
    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return mapper;
    }

    /**
     * AntPathMatcher：用于白名单路径匹配
     */
    @Bean
    public AntPathMatcher antPathMatcher() {
        return new AntPathMatcher();
    }
}
