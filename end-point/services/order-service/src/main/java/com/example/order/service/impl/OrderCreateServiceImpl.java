package com.example.order.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
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
        // 0. 检查用户是否已有待支付的订单（防止一个用户预约多个桩）
        LambdaQueryWrapper<Order> userOrderCheck = new LambdaQueryWrapper<>();
        userOrderCheck.eq(Order::getUserId, userId).eq(Order::getStatus, 0);
        long pendingOrderCount = this.count(userOrderCheck);
        if (pendingOrderCount > 0) {
            throw new RuntimeException("您有未支付的订单，请先完成支付或取消");
        }

        // 分布式锁的 Key：同一时刻只有一个用户能抢到同一个桩
        String lockKey = "lock:station:" + stationId;
        RLock lock = redissonClient.getLock(lockKey);

        try {
            // 尝试抢锁：最多等待 3 秒，抢到后锁定 30 秒
            boolean isLocked = lock.tryLock(3, 30, TimeUnit.SECONDS);
            if (!isLocked) {
                throw new RuntimeException("充电桩当前繁忙，请稍后再试");
            }

            // 1. 检查充电桩状态：只有空闲(0)的桩才能预约
            // 再次检查充电桩状态（防止并发问题）
            LambdaQueryWrapper<Order> stationOrderCheck = new LambdaQueryWrapper<>();
            stationOrderCheck.eq(Order::getStationId, stationId)
                    .in(Order::getStatus, 0, 1);  // status=0(待支付) 或 status=1(充电中)
            long existingOrder = this.count(stationOrderCheck);
            if (existingOrder > 0) {
                throw new RuntimeException("充电桩当前繁忙，请稍后再试");
            }

            // 2. 通过 Feign 将充电桩状态改为【预约中(1)】
            R result = stationFeignClient.updateStationStatus(stationId, 1);
            
            // 检查 Feign 调用是否成功
            log.info("Feign 返回结果 | result: {} | code: {}", result, result != null ? result.getCode() : "null");
            if (result == null || result.getCode() == null || !result.getCode().equals(200)) {
                throw new RuntimeException("充电桩状态更新失败，请稍后重试");
            }
            log.info("充电桩已锁定为预约中 | stationId: {} | station.status: 1", stationId);

            // 3. 生成订单号（时间戳 + 随机4位）
            String orderNo = System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 4).toUpperCase();

            // 4. 创建订单并写入数据库（status=0 待支付）
            Order order = new Order();
            order.setOrderNo(orderNo);
            order.setUserId(userId);
            order.setStationId(stationId);
            order.setStatus(0);  // 待支付
            this.save(order);
            log.info("订单创建成功 | orderNo: {} | userId: {} | stationId: {} | order.status: 0", orderNo, userId, stationId);

            // 5. 发消息到 RabbitMQ 普通队列，15分钟后自动转入死信队列触发取消
            rabbitTemplate.convertAndSend(RabbitMQConfig.ORDER_EXCHANGE, RabbitMQConfig.ORDER_ROUTING_KEY, orderNo);
            log.info("超时取消消息已发送 | orderNo: {} | 将在 15 分钟后自动取消", orderNo);

            return orderNo;

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("抢锁被中断，请重试");
        } finally {
            // 确保锁一定被释放，防止死锁
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
                log.info("分布式锁已释放 | lockKey: {}", lockKey);
            }
        }
    }
}
