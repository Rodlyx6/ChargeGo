package com.example.order.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.common.enums.OrderStatusEnum;
import com.example.common.enums.StationStatusEnum;
import com.example.common.exception.BusinessException;
import com.example.common.result.R;
import com.example.order.config.RabbitMQConfig;
import com.example.order.entity.Order;
import com.example.order.feign.StationFeignClient;
import com.example.order.mapper.OrderMapper;
import com.example.order.model.vo.OrderVO;
import com.example.order.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 订单服务实现（统一）
 * 整合了创建、支付、取消、查询等所有订单相关功能
 */
@Slf4j
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {

    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private StationFeignClient stationFeignClient;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    // ==================== 创建订单 ====================

    @Override
    public String createOrder(Long userId, Long stationId) {
        checkUserPendingOrder(userId);

        String lockKey = "lock:station:" + stationId;
        RLock lock = redissonClient.getLock(lockKey);

        try {
            if (!lock.tryLock(3, 30, TimeUnit.SECONDS)) {
                throw new BusinessException("充电桩当前繁忙，请稍后再试");
            }

            checkStationAvailable(stationId);
            lockStation(stationId);

            String orderNo = generateOrderNo();
            createOrderRecord(userId, stationId, orderNo);
            sendTimeoutMessage(orderNo);

            return orderNo;

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new BusinessException("抢锁被中断，请重试");
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    // ==================== 支付订单 ====================

    @Override
    public void payOrder(String orderNo, Long userId) {
        Order order = getOrderByNo(orderNo);
        checkOrderOwner(order, userId);

        if (!OrderStatusEnum.PENDING_PAYMENT.getCode().equals(order.getStatus())) {
            throw new BusinessException("只有待支付的订单才能支付");
        }

        order.setStatus(OrderStatusEnum.CHARGING.getCode());
        order.setPayTime(LocalDateTime.now());
        order.setUpdateTime(LocalDateTime.now());
        this.updateById(order);
        log.info("订单已支付 | orderNo: {} | userId: {}", orderNo, userId);

        stationFeignClient.updateStationStatus(order.getStationId(), StationStatusEnum.CHARGING.getCode());
    }

    // ==================== 取消订单 ====================

    @Override
    public void cancelPayment(String orderNo, Long userId) {
        Order order = getOrderByNo(orderNo);
        checkOrderOwner(order, userId);

        if (!OrderStatusEnum.PENDING_PAYMENT.getCode().equals(order.getStatus())) {
            throw new BusinessException("只有待支付的订单才能取消");
        }

        cancelOrderAndReleaseStation(order, OrderStatusEnum.CANCELLED.getCode());
        log.info("订单已取消支付 | orderNo: {} | userId: {}", orderNo, userId);
    }

    @Override
    public void cancelOrder(String orderNo, Long userId) {
        Order order = getOrderByNo(orderNo);
        checkOrderOwner(order, userId);

        if (!OrderStatusEnum.CHARGING.getCode().equals(order.getStatus())) {
            throw new BusinessException("只有充电中的订单才能取消");
        }

        cancelOrderAndReleaseStation(order, OrderStatusEnum.COMPLETED.getCode());
        log.info("订单已取消 | orderNo: {} | userId: {}", orderNo, userId);
    }

    @Override
    public void cancelTimeoutOrder(String orderNo) {
        LambdaQueryWrapper<Order> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Order::getOrderNo, orderNo);
        Order order = this.getOne(queryWrapper);

        if (order == null) {
            log.warn("超时取消：订单不存在 | orderNo: {}", orderNo);
            return;
        }

        if (!OrderStatusEnum.PENDING_PAYMENT.getCode().equals(order.getStatus())) {
            log.info("超时取消：订单已处理 | orderNo: {} | status: {}", orderNo, order.getStatus());
            return;
        }

        cancelOrderAndReleaseStation(order, OrderStatusEnum.CANCELLED.getCode());
        log.info("订单已超时取消 | orderNo: {}", orderNo);
    }

    // ==================== 查询订单 ====================

    @Override
    public List<OrderVO> getOrderList(Long userId) {
        LambdaQueryWrapper<Order> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Order::getUserId, userId)
                .orderByDesc(Order::getCreateTime);

        List<Order> orders = this.list(queryWrapper);
        log.info("查询用户订单 | userId: {} | 订单数量: {}", userId, orders.size());

        return orders.stream().map(this::convertToVO).collect(Collectors.toList());
    }

    @Override
    public OrderVO getOrderDetail(String orderNo, Long userId) {
        Order order = getOrderByNo(orderNo);
        checkOrderOwner(order, userId);

        log.info("查询订单详情 | orderNo: {} | userId: {}", orderNo, userId);
        return convertToVO(order);
    }

    // ==================== 私有方法 ====================

    private void checkUserPendingOrder(Long userId) {
        long count = this.lambdaQuery()
                .eq(Order::getUserId, userId)
                .eq(Order::getStatus, OrderStatusEnum.PENDING_PAYMENT.getCode())
                .count();
        if (count > 0) {
            throw new BusinessException("您有未支付的订单，请先完成支付或取消");
        }
    }

    private void checkStationAvailable(Long stationId) {
        long count = this.lambdaQuery()
                .eq(Order::getStationId, stationId)
                .in(Order::getStatus, OrderStatusEnum.PENDING_PAYMENT.getCode(), OrderStatusEnum.CHARGING.getCode())
                .count();
        if (count > 0) {
            throw new BusinessException("充电桩当前繁忙，请稍后再试");
        }
    }

    private void lockStation(Long stationId) {
        R result = stationFeignClient.updateStationStatus(stationId, StationStatusEnum.RESERVED.getCode());
        if (result == null || !Integer.valueOf(200).equals(result.getCode())) {
            throw new BusinessException("充电桩状态更新失败，请稍后重试");
        }
        log.info("充电桩已锁定 | stationId: {}", stationId);
    }

    private String generateOrderNo() {
        return System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 4).toUpperCase();
    }

    private void createOrderRecord(Long userId, Long stationId, String orderNo) {
        Order order = new Order();
        order.setOrderNo(orderNo);
        order.setUserId(userId);
        order.setStationId(stationId);
        order.setStatus(OrderStatusEnum.PENDING_PAYMENT.getCode());
        this.save(order);
        log.info("订单创建成功 | orderNo: {} | userId: {} | stationId: {}", orderNo, userId, stationId);
    }

    private void sendTimeoutMessage(String orderNo) {
        rabbitTemplate.convertAndSend(RabbitMQConfig.ORDER_EXCHANGE, RabbitMQConfig.ORDER_ROUTING_KEY, orderNo);
        log.info("超时取消消息已发送 | orderNo: {}", orderNo);
    }

    private void cancelOrderAndReleaseStation(Order order, Integer newStatus) {
        order.setStatus(newStatus);
        order.setUpdateTime(LocalDateTime.now());
        this.updateById(order);

        try {
            stationFeignClient.updateStationStatus(order.getStationId(), StationStatusEnum.IDLE.getCode());
            log.info("充电桩已释放 | stationId: {}", order.getStationId());
        } catch (Exception e) {
            log.error("释放充电桩失败 | stationId: {} | error: {}", order.getStationId(), e.getMessage());
        }
    }

    private Order getOrderByNo(String orderNo) {
        LambdaQueryWrapper<Order> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Order::getOrderNo, orderNo);
        Order order = this.getOne(queryWrapper);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        return order;
    }

    private void checkOrderOwner(Order order, Long userId) {
        if (!order.getUserId().equals(userId)) {
            throw new BusinessException(403, "无权操作他人的订单");
        }
    }

    private OrderVO convertToVO(Order order) {
        OrderVO vo = new OrderVO();
        vo.setOrderNo(order.getOrderNo());
        vo.setUserId(String.valueOf(order.getUserId()));
        vo.setStationId(String.valueOf(order.getStationId()));
        vo.setStatus(order.getStatus());
        vo.setStatusDesc(OrderStatusEnum.getDesc(order.getStatus()));
        vo.setPayTime(order.getPayTime());
        vo.setCreateTime(order.getCreateTime());
        vo.setUpdateTime(order.getUpdateTime());
        return vo;
    }
}
