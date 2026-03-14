package com.example.order.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.common.constant.OrderStatus;
import com.example.common.constant.StationStatus;
import com.example.common.exception.BusinessException;
import com.example.common.result.R;
import com.example.order.config.RabbitMQConfig;
import com.example.order.entity.Order;
import com.example.order.feign.StationFeignClient;
import com.example.order.mapper.OrderMapper;
import com.example.order.service.OrderCreateService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class OrderCreateServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderCreateService {

    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private StationFeignClient stationFeignClient;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Override
    public String createOrder(Long userId, Long stationId) {
        // 检查用户是否已有待支付的订单
        checkUserPendingOrder(userId);

        // 分布式锁
        String lockKey = "lock:station:" + stationId;
        RLock lock = redissonClient.getLock(lockKey);

        try {
            if (!lock.tryLock(3, 30, TimeUnit.SECONDS)) {
                throw new BusinessException("充电桩当前繁忙，请稍后再试");
            }

            // 检查充电桩是否可用
            checkStationAvailable(stationId);

            // 锁定充电桩
            lockStation(stationId);

            // 生成订单
            String orderNo = generateOrderNo();
            createOrderRecord(userId, stationId, orderNo);

            // 发送超时取消消息
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

    /**
     * 检查用户是否有待支付订单
     */
    private void checkUserPendingOrder(Long userId) {
        long count = this.lambdaQuery()
                .eq(Order::getUserId, userId)
                .eq(Order::getStatus, OrderStatus.PENDING_PAYMENT)
                .count();
        if (count > 0) {
            throw new BusinessException("您有未支付的订单，请先完成支付或取消");
        }
    }

    /**
     * 检查充电桩是否可用
     */
    private void checkStationAvailable(Long stationId) {
        long count = this.lambdaQuery()
                .eq(Order::getStationId, stationId)
                .in(Order::getStatus, OrderStatus.PENDING_PAYMENT, OrderStatus.CHARGING)
                .count();
        if (count > 0) {
            throw new BusinessException("充电桩当前繁忙，请稍后再试");
        }
    }

    /**
     * 锁定充电桩
     */
    private void lockStation(Long stationId) {
        R result = stationFeignClient.updateStationStatus(stationId, StationStatus.RESERVED);
        if (result == null || !Integer.valueOf(200).equals(result.getCode())) {
            throw new BusinessException("充电桩状态更新失败，请稍后重试");
        }
        log.info("充电桩已锁定 | stationId: {}", stationId);
    }

    /**
     * 生成订单号
     */
    private String generateOrderNo() {
        return System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 4).toUpperCase();
    }

    /**
     * 创建订单记录
     */
    private void createOrderRecord(Long userId, Long stationId, String orderNo) {
        Order order = new Order();
        order.setOrderNo(orderNo);
        order.setUserId(userId);
        order.setStationId(stationId);
        order.setStatus(OrderStatus.PENDING_PAYMENT);
        this.save(order);
        log.info("订单创建成功 | orderNo: {} | userId: {} | stationId: {}", orderNo, userId, stationId);
    }

    /**
     * 发送超时取消消息
     */
    private void sendTimeoutMessage(String orderNo) {
        rabbitTemplate.convertAndSend(RabbitMQConfig.ORDER_EXCHANGE, RabbitMQConfig.ORDER_ROUTING_KEY, orderNo);
        log.info("超时取消消息已发送 | orderNo: {}", orderNo);
    }
}
