package com.example.order.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.common.enums.OrderStatusEnum;
import com.example.common.enums.StationStatusEnum;
import com.example.common.exception.BusinessException;
import com.example.common.result.R;
import com.example.order.config.RabbitMQConfig;
import com.example.order.entity.Order;
import com.example.order.enums.ChargeTypeEnum;
import com.example.order.enums.ChargeTypeEnum;
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
    public String createOrder(Long userId, Long stationId, Integer chargeType, Integer chargeTime) {
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
            
            // 计算预期金额
            Double expectedAmount = calculateExpectedAmount(chargeType, chargeTime);
            
            createOrderRecord(userId, stationId, orderNo, chargeType, chargeTime, expectedAmount);
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

        // 计算实际充电时间（分钟转小时，保留2位小数）
        LocalDateTime now = LocalDateTime.now();
        long chargedMinutes = java.time.Duration.between(order.getPayTime(), now).toMinutes();
        double actualChargeTime = Math.round(chargedMinutes / 60.0 * 100) / 100.0;

        // 计算实际金额
        ChargeTypeEnum chargeTypeEnum = ChargeTypeEnum.getByCode(order.getChargeType());
        double actualAmount = chargeTypeEnum != null 
                ? Math.round(chargeTypeEnum.getPricePerHour() * actualChargeTime * 100) / 100.0
                : 0.0;

        // 计算退款金额 = 预期金额 - 实际金额
        double expectedAmount = order.getExpectedAmount() != null ? order.getExpectedAmount() : 0.0;
        double refundAmount = Math.max(0, Math.round((expectedAmount - actualAmount) * 100) / 100.0);

        order.setActualAmount(actualAmount);
        order.setRefundAmount(refundAmount);
        cancelOrderAndReleaseStation(order, OrderStatusEnum.COMPLETED.getCode());
        
        log.info("订单已取消 | orderNo: {} | actualChargeTime: {}h | actualAmount: {} | refundAmount: {}",
                orderNo, actualChargeTime, actualAmount, refundAmount);
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

    /**
     * 计算预期金额
     */
    private Double calculateExpectedAmount(Integer chargeType, Integer chargeTime) {
        if (chargeType == null || chargeTime == null) {
            throw new BusinessException("充电类型和充电时间不能为空");
        }
        
        ChargeTypeEnum chargeTypeEnum = ChargeTypeEnum.getByCode(chargeType);
        if (chargeTypeEnum == null) {
            throw new BusinessException("充电类型不存在");
        }
        
        return chargeTypeEnum.calculatePrice(chargeTime);
    }

    private void createOrderRecord(Long userId, Long stationId, String orderNo, Integer chargeType, Integer chargeTime, Double expectedAmount) {
        Order order = new Order();
        order.setOrderNo(orderNo);
        order.setUserId(userId);
        order.setStationId(stationId);
        order.setStatus(OrderStatusEnum.PENDING_PAYMENT.getCode());
        order.setChargeType(chargeType);
        order.setChargeTime(chargeTime);
        order.setExpectedAmount(expectedAmount);
        order.setCreateTime(LocalDateTime.now());
        order.setUpdateTime(LocalDateTime.now());
        this.save(order);
        log.info("订单创建成功 | orderNo: {} | userId: {} | stationId: {} | chargeType: {} | chargeTime: {} | expectedAmount: {}", 
                orderNo, userId, stationId, chargeType, chargeTime, expectedAmount);
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
        
        // 充电相关信息
        vo.setChargeType(order.getChargeType());
        if (order.getChargeType() != null) {
            ChargeTypeEnum chargeTypeEnum = ChargeTypeEnum.getByCode(order.getChargeType());
            vo.setChargeTypeDesc(chargeTypeEnum != null ? chargeTypeEnum.getDesc() : "未知");
        }
        vo.setChargeTime(order.getChargeTime());
        vo.setExpectedAmount(order.getExpectedAmount());
        vo.setActualAmount(order.getActualAmount());
        vo.setRefundAmount(order.getRefundAmount());
        
        // 实际充电时间 = 结束时间(updateTime) - 支付时间(payTime)
        if (order.getPayTime() != null && order.getUpdateTime() != null
                && OrderStatusEnum.COMPLETED.getCode().equals(order.getStatus())) {
            long minutes = java.time.Duration.between(order.getPayTime(), order.getUpdateTime()).toMinutes();
            double actualChargeTime = Math.round(minutes / 60.0 * 100) / 100.0;
            vo.setActualChargeTime(actualChargeTime);
        }
        
        vo.setPayTime(order.getPayTime());
        vo.setCreateTime(order.getCreateTime());
        vo.setUpdateTime(order.getUpdateTime());
        return vo;
    }
}
