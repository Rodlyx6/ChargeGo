package com.example.order.controller;

import com.example.common.result.R;
import com.example.order.dto.PaymentRequest;
import com.example.order.service.OrderCancelService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/order")
public class OrderCancelController {

    @Autowired
    private OrderCancelService orderCancelService;

    /**
     * 取消支付（待支付时取消）
     */
    @PostMapping("/cancelPayment")
    public R cancelPayment(@RequestHeader("X-User-Id") Long userId,
                           @RequestBody PaymentRequest request) {
        orderCancelService.cancelPayment(request.getOrderNo(), userId);
        return R.ok("取消成功", null);
    }

    /**
     * 取消订单（充电中时取消）
     */
    @PostMapping("/cancelOrder")
    public R cancelOrder(@RequestHeader("X-User-Id") Long userId,
                         @RequestBody PaymentRequest request) {
        orderCancelService.cancelOrder(request.getOrderNo(), userId);
        return R.ok("取消成功", null);
    }
}
