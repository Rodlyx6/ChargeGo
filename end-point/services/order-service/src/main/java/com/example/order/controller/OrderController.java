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
 * 订单控制器（统一）
 * 整合了创建、支付、取消、查询等所有订单相关接口
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
        Long stationId = Long.parseLong(request.getStationId());
        
        log.info("📥 收到预约请求 | userId: {} | stationId(原始): {} | stationId(转换后): {}", 
                userId, request.getStationId(), stationId);
        
        String orderNo = orderService.createOrder(userId, stationId);
        
        log.info("✅ 订单创建成功 | orderNo: {}", orderNo);
        return R.ok("预约成功", orderNo);
    }

    /**
     * 支付订单
     */
    @PostMapping("/pay")
    public R payOrder(@RequestHeader("X-User-Id") Long userId,
                      @RequestBody OrderPayDTO request) {
        log.info("📥 收到支付请求 | userId: {} | orderNo: {}", userId, request.getOrderNo());
        
        orderService.payOrder(request.getOrderNo(), userId);
        
        log.info("✅ 支付成功 | orderNo: {}", request.getOrderNo());
        return R.ok("支付成功", null);
    }

    /**
     * 取消支付（待支付时取消）
     */
    @PostMapping("/cancelPayment")
    public R cancelPayment(@RequestHeader("X-User-Id") Long userId,
                           @RequestBody OrderCancelDTO request) {
        log.info("📥 收到取消支付请求 | userId: {} | orderNo: {}", userId, request.getOrderNo());
        
        orderService.cancelPayment(request.getOrderNo(), userId);
        
        log.info("✅ 取消支付成功 | orderNo: {}", request.getOrderNo());
        return R.ok("取消成功", null);
    }

    /**
     * 取消订单（充电中时取消，即结束充电）
     */
    @PostMapping("/cancelOrder")
    public R cancelOrder(@RequestHeader("X-User-Id") Long userId,
                         @RequestBody OrderCancelDTO request) {
        log.info("📥 收到取消订单请求 | userId: {} | orderNo: {}", userId, request.getOrderNo());
        
        orderService.cancelOrder(request.getOrderNo(), userId);
        
        log.info("✅ 取消订单成功 | orderNo: {}", request.getOrderNo());
        return R.ok("操作成功", null);
    }

    /**
     * 查询当前用户的所有订单
     */
    @GetMapping("/list")
    public R getMyOrders(@RequestHeader("X-User-Id") Long userId) {
        log.info("📋 查询用户订单列表 | userId: {}", userId);
        
        List<OrderVO> orders = orderService.getOrderList(userId);
        
        return R.ok("查询成功", orders);
    }

    /**
     * 查询订单详情
     */
    @GetMapping("/detail/{orderNo}")
    public R getOrderDetail(@RequestHeader("X-User-Id") Long userId,
                            @PathVariable String orderNo) {
        log.info("📋 查询订单详情 | userId: {} | orderNo: {}", userId, orderNo);
        
        OrderVO order = orderService.getOrderDetail(orderNo, userId);
        
        return R.ok("查询成功", order);
    }

    /**
     * 测试接口
     */
    @GetMapping("/hello")
    public String hello() {
        return "hello order-service";
    }
}
