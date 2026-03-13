package com.example.order.service;

/**
 * 订单取消服务接口
 */
public interface OrderCancelService {

    /**
     * 取消支付（用户在待支付时主动取消）
     * 流程：order.status 0→3，station.status 1→0，update_time 更新
     *
     * @param orderNo 订单号
     * @param userId  当前用户ID（用于权限检查）
     */
    void cancelPayment(String orderNo, Long userId);

    /**
     * 取消订单（用户在充电中时主动取消）
     * 流程：order.status 1→2，station.status 2→0，update_time 更新为当前时间（用于计算使用时长）
     *
     * @param orderNo 订单号
     * @param userId  当前用户ID（用于权限检查）
     */
    void cancelOrder(String orderNo, Long userId);

    /**
     * 处理订单超时取消（15分钟未支付）
     * 由 RabbitMQ 死信队列消费者调用
     * 流程：order.status 0→3，station.status 1→0，update_time 更新
     *
     * @param orderNo 订单号
     */
    void cancelTimeoutOrder(String orderNo);
}
