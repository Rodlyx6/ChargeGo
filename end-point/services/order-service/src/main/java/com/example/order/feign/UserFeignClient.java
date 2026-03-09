package com.example.order.feign;
import com.example.common.entity.UserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
@FeignClient(value = "user-service")
public interface UserFeignClient {
    @GetMapping("/user/{id}")
    String getUserById(@PathVariable("id") Long id);
}
