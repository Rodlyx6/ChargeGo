package com.example.order.controller;

import com.example.common.result.R;
import com.example.order.dto.PaymentRequest;
import com.example.order.service.OrderPaymentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/order")
public class OrderPaymentController {

    @Autowired
    private OrderPaymentService orderPaymentService;

    /**
     * 支付订单
     * order.status: 0 → 1
     * station.status: 1 → 2
     * pay_time 和 update_time 更新
     */
    @PostMapping("/pay")
    public R payOrder(@RequestHeader("X-User-Id") Long userId,
                      @RequestBody PaymentRequest request) {
        try {
            orderPaymentService.payOrder(request.getOrderNo(), userId);
            return R.ok("支付成功", null);
        } catch (Exception e) {
            return R.error(500, e.getMessage());
        }
    }
}
