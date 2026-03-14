package com.example.order.controller;

import com.example.common.result.R;
import com.example.order.dto.CreateOrderRequest;
import com.example.order.service.OrderCreateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/order")
public class OrderCreateController {

    @Autowired
    private OrderCreateService orderCreateService;

    /**
     * 创建预约订单
     */
    @PostMapping("/create")
    public R createOrder(@RequestHeader("X-User-Id") Long userId,
                         @RequestBody CreateOrderRequest request) {
        String orderNo = orderCreateService.createOrder(userId, request.getStationId());
        return R.ok("预约成功", orderNo);
    }

    /**
     * 测试接口
     */
    @GetMapping("/hello")
    public String hello() {
        return "hello order-service";
    }
}
