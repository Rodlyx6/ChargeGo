package com.example.order.feign.fallback;

import com.example.order.feign.UserFeignClient;
import org.springframework.stereotype.Component;

@Component
public class UserFeignClientFallback implements UserFeignClient {
    @Override
    public String getUserById(Long id) {
        return "未知用户";
    }
}
