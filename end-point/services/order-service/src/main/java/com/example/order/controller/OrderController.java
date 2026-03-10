package com.example.order.controller;

import com.example.order.feign.UserFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/order")
public class OrderController {
    @Autowired
    private UserFeignClient userFeignClient;;
    @GetMapping("/hello")
    public String hello() {
        return "hello order";
    }

    @GetMapping("/user/{id}")
    public String getUserById(@PathVariable Long id) {
        return userFeignClient.getUserById(id);
    }
}
