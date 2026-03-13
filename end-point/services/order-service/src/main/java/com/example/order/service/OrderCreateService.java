package com.example.order.service;

/**
 * 订单创建服务接口
 */
public interface OrderCreateService {

    /**
     * 创建预约订单
     * 流程：检查用户是否有待支付订单 → Redisson 分布式锁 → 检查桩状态 → Feign 改桩状态为预约中(1) → 生成订单(status=0) → RabbitMQ 发超时消息
     *
     * @param userId    下单用户ID
     * @param stationId 要预约的充电桩ID
     * @return 生成的订单号
     */
    String createOrder(Long userId, Long stationId);
}
