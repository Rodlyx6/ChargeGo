package com.example.order.service;

import com.example.order.entity.Order;
import com.example.order.model.vo.OrderVO;

import java.util.List;

/**
 * 订单服务接口（统一）
 * 整合了创建、支付、取消、查询等所有订单相关功能
 */
public interface OrderService {

    /**
     * 创建预约订单
     * 流程：检查用户是否有待支付订单 → Redisson 分布式锁 → 检查桩状态 → Feign 改桩状态为预约中(1) → 生成订单(status=0) → RabbitMQ 发超时消息
     *
     * @param userId      下单用户ID
     * @param stationId   要预约的充电桩ID
     * @param chargeType  充电类型（1快充 2普通 3慢充）
     * @param chargeTime  充电时间（小时）
     * @return 生成的订单号
     */
    String createOrder(Long userId, Long stationId, Integer chargeType, Integer chargeTime);

    /**
     * 支付订单
     * 流程：order.status 0→1，station.status 1→2，pay_time 和 update_time 更新
     *
     * @param orderNo 订单号
     * @param userId  当前用户ID（用于权限检查）
     */
    void payOrder(String orderNo, Long userId);

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

    /**
     * 查询用户的所有订单
     * @param userId 用户ID
     * @return 订单列表
     */
    List<OrderVO> getOrderList(Long userId);

    /**
     * 查询订单详情
     * @param orderNo 订单号
     * @param userId 用户ID（用于权限检查）
     * @return 订单详情
     */
    OrderVO getOrderDetail(String orderNo, Long userId);
}
