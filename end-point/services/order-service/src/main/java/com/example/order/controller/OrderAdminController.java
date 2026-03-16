package com.example.order.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.common.result.R;
import com.example.order.model.vo.OrderVO;
import com.example.order.service.OrderAdminService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 订单管理控制器（管理员端）
 * 职责：处理管理员端的订单查询、统计等接口
 * 权限：仅管理员可访问（通过网关权限验证）
 */
@Slf4j
@RestController
@RequestMapping("/admin/order")
public class OrderAdminController {

    @Autowired
    private OrderAdminService orderAdminService;

    /**
     * 分页查询订单列表
     */
    @GetMapping("/list")
    public R getOrderList(@RequestHeader("X-User-Id") Long adminId,
                          @RequestParam(defaultValue = "1") Integer pageNum,
                          @RequestParam(defaultValue = "10") Integer pageSize,
                          @RequestParam(required = false) Integer status,
                          @RequestParam(required = false) Long userId) {
        log.info("📋 查询订单列表 | adminId: {} | pageNum: {} | pageSize: {} | status: {} | userId: {}", 
                adminId, pageNum, pageSize, status, userId);

        Page<OrderVO> result = orderAdminService.getOrderPage(pageNum, pageSize, status, userId);
        return R.ok("查询成功", result);
    }

    /**
     * 查询订单详情
     */
    @GetMapping("/{orderNo}")
    public R getOrderDetail(@RequestHeader("X-User-Id") Long adminId,
                            @PathVariable String orderNo) {
        log.info("🔍 查询订单详情 | adminId: {} | orderNo: {}", adminId, orderNo);

        OrderVO order = orderAdminService.getOrderDetail(orderNo);
        return R.ok("查询成功", order);
    }

    /**
     * 获取订单统计数据
     */
    @GetMapping("/stats")
    public R getOrderStats(@RequestHeader("X-User-Id") Long adminId) {
        log.info("📊 查询订单统计 | adminId: {}", adminId);

        Map<String, Object> stats = orderAdminService.getOrderStats();
        return R.ok("查询成功", stats);
    }
}
