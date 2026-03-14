package com.example.order.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.common.constant.OrderStatus;
import com.example.common.constant.StationStatus;
import com.example.common.exception.BusinessException;
import com.example.order.entity.Order;
import com.example.order.feign.StationFeignClient;
import com.example.order.mapper.OrderMapper;
import com.example.order.service.OrderCancelService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
public class OrderCancelServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderCancelService {

    @Autowired
    private StationFeignClient stationFeignClient;

    @Override
    public void cancelPayment(String orderNo, Long userId) {
        Order order = getOrderByNo(orderNo);
        checkOrderOwner(order, userId);
        
        if (!OrderStatus.PENDING_PAYMENT.equals(order.getStatus())) {
            throw new BusinessException("只有待支付的订单才能取消");
        }

        cancelOrderAndReleaseStation(order, OrderStatus.CANCELLED);
        log.info("订单已取消支付 | orderNo: {} | userId: {}", orderNo, userId);
    }

    @Override
    public void cancelOrder(String orderNo, Long userId) {
        Order order = getOrderByNo(orderNo);
        checkOrderOwner(order, userId);
        
        if (!OrderStatus.CHARGING.equals(order.getStatus())) {
            throw new BusinessException("只有充电中的订单才能取消");
        }

        cancelOrderAndReleaseStation(order, OrderStatus.COMPLETED);
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

        if (!OrderStatus.PENDING_PAYMENT.equals(order.getStatus())) {
            log.info("超时取消：订单已处理 | orderNo: {} | status: {}", orderNo, order.getStatus());
            return;
        }

        cancelOrderAndReleaseStation(order, OrderStatus.CANCELLED);
        log.info("订单已超时取消 | orderNo: {}", orderNo);
    }

    /**
     * 取消订单并释放充电桩
     */
    private void cancelOrderAndReleaseStation(Order order, Integer newStatus) {
        order.setStatus(newStatus);
        order.setUpdateTime(LocalDateTime.now());
        this.updateById(order);
        
        try {
            stationFeignClient.updateStationStatus(order.getStationId(), StationStatus.IDLE);
            log.info("充电桩已释放 | stationId: {}", order.getStationId());
        } catch (Exception e) {
            log.error("释放充电桩失败 | stationId: {} | error: {}", order.getStationId(), e.getMessage());
        }
    }

    /**
     * 根据订单号查询订单
     */
    private Order getOrderByNo(String orderNo) {
        LambdaQueryWrapper<Order> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Order::getOrderNo, orderNo);
        Order order = this.getOne(queryWrapper);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        return order;
    }

    /**
     * 检查订单所有者
     */
    private void checkOrderOwner(Order order, Long userId) {
        if (!order.getUserId().equals(userId)) {
            throw new BusinessException(403, "无权操作他人的订单");
        }
    }
}
