package com.example.order.controller;

import com.example.common.result.R;
import com.example.order.model.dto.OrderCancelDTO;
import com.example.order.model.dto.OrderCreateDTO;
import com.example.order.model.dto.OrderPayDTO;
import com.example.order.model.vo.OrderVO;
import com.example.order.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 订单控制器（用户端）
 * 职责：处理用户端的订单创建、支付、取消、查询等接口
 * 权限：需要用户认证
 */
@Slf4j
@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    /**
     * 创建预约订单
     */
    @PostMapping("/create")
    public R createOrder(@RequestHeader("X-User-Id") Long userId,
                         @RequestBody OrderCreateDTO request) {
        log.info("📥 创建订单 | userId: {} | stationId: {}", userId, request.getStationId());
        
        Long stationId = Long.parseLong(request.getStationId());
        String orderNo = orderService.createOrder(userId, stationId);
        
        return R.ok("预约成功", orderNo);
    }

    /**
     * 支付订单
     */
    @PostMapping("/pay")
    public R payOrder(@RequestHeader("X-User-Id") Long userId,
                      @RequestBody OrderPayDTO request) {
        log.info("💳 支付订单 | userId: {} | orderNo: {}", userId, request.getOrderNo());
        
        orderService.payOrder(request.getOrderNo(), userId);
        return R.ok("支付成功", null);
    }

    /**
     * 取消支付（待支付时取消）
     */
    @PostMapping("/cancelPayment")
    public R cancelPayment(@RequestHeader("X-User-Id") Long userId,
                           @RequestBody OrderCancelDTO request) {
        log.info("❌ 取消支付 | userId: {} | orderNo: {}", userId, request.getOrderNo());
        
        orderService.cancelPayment(request.getOrderNo(), userId);
        return R.ok("取消成功", null);
    }

    /**
     * 取消订单（充电中时取消，即结束充电）
     */
    @PostMapping("/cancelOrder")
    public R cancelOrder(@RequestHeader("X-User-Id") Long userId,
                         @RequestBody OrderCancelDTO request) {
        log.info("⏹️ 结束充电 | userId: {} | orderNo: {}", userId, request.getOrderNo());
        
        orderService.cancelOrder(request.getOrderNo(), userId);
        return R.ok("操作成功", null);
    }

    /**
     * 查询我的订单列表
     */
    @GetMapping("/list")
    public R getMyOrders(@RequestHeader("X-User-Id") Long userId) {
        log.info("📋 查询订单列表 | userId: {}", userId);
        
        List<OrderVO> orders = orderService.getOrderList(userId);
        return R.ok("查询成功", orders);
    }

    /**
     * 查询订单详情
     */
    @GetMapping("/{orderNo}")
    public R getOrderDetail(@RequestHeader("X-User-Id") Long userId,
                            @PathVariable String orderNo) {
        log.info("🔍 查询订单详情 | userId: {} | orderNo: {}", userId, orderNo);
        
        OrderVO order = orderService.getOrderDetail(orderNo, userId);
        return R.ok("查询成功", order);
    }
}
