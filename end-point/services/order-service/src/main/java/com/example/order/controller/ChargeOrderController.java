package com.example.order.controller;

import com.example.common.result.R;
import com.example.order.model.dto.ChargeOrderCompleteDTO;
import com.example.order.model.dto.ChargeOrderCreateDTO;
import com.example.order.model.dto.ChargeOrderPayDTO;
import com.example.order.model.vo.ChargeOrderVO;
import com.example.order.service.ChargeOrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 充电订单控制器
 * 职责：处理充电订单的创建、支付、完成等接口
 * 权限：需要用户认证
 */
@Slf4j
@RestController
@RequestMapping("/charge")
public class ChargeOrderController {

    @Autowired
    private ChargeOrderService chargeOrderService;

    /**
     * 创建充电订单（计算预期金额）
     */
    @PostMapping("/create")
    public R createChargeOrder(@RequestHeader("X-User-Id") Long userId,
                               @RequestBody ChargeOrderCreateDTO request) {
        log.info("💡 创建充电订单 | userId: {} | chargeType: {} | chargeTime: {}分钟", 
                userId, request.getChargeType(), request.getChargeTime());
        
        String orderNo = chargeOrderService.createChargeOrder(userId, request);
        return R.ok("预约成功", orderNo);
    }

    /**
     * 支付充电订单
     */
    @PostMapping("/pay")
    public R payChargeOrder(@RequestHeader("X-User-Id") Long userId,
                            @RequestBody ChargeOrderPayDTO request) {
        log.info("💳 支付充电订单 | userId: {} | orderNo: {} | actualAmount: {}元", 
                userId, request.getOrderNo(), request.getActualAmount());
        
        chargeOrderService.payChargeOrder(userId, request);
        return R.ok("支付成功", null);
    }

    /**
     * 完成充电订单（计算实际金额和退款）
     */
    @PostMapping("/complete")
    public R completeChargeOrder(@RequestHeader("X-User-Id") Long userId,
                                 @RequestBody ChargeOrderCompleteDTO request) {
        log.info("⏹️ 完成充电订单 | userId: {} | orderNo: {} | actualChargeTime: {}分钟", 
                userId, request.getOrderNo(), request.getActualChargeTime());
        
        ChargeOrderVO result = chargeOrderService.completeChargeOrder(userId, request);
        return R.ok("完成成功", result);
    }

    /**
     * 查询充电订单详情
     */
    @GetMapping("/{orderNo}")
    public R getChargeOrderDetail(@RequestHeader("X-User-Id") Long userId,
                                  @PathVariable String orderNo) {
        log.info("🔍 查询充电订单详情 | userId: {} | orderNo: {}", userId, orderNo);
        
        ChargeOrderVO order = chargeOrderService.getChargeOrderDetail(userId, orderNo);
        return R.ok("查询成功", order);
    }
}
