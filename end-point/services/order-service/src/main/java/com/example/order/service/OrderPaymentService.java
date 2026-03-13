package com.example.order.service;

/**
 * 订单支付服务接口
 */
public interface OrderPaymentService {

    /**
     * 支付订单
     * 流程：order.status 0→1，station.status 1→2，pay_time 和 update_time 更新
     *
     * @param orderNo 订单号
     * @param userId  当前用户ID（用于权限检查）
     */
    void payOrder(String orderNo, Long userId);
}
