package com.example.order.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.common.constant.OrderStatus;
import com.example.common.constant.StationStatus;
import com.example.common.exception.BusinessException;
import com.example.order.entity.Order;
import com.example.order.feign.StationFeignClient;
import com.example.order.mapper.OrderMapper;
import com.example.order.service.OrderPaymentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
public class OrderPaymentServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderPaymentService {

    @Autowired
    private StationFeignClient stationFeignClient;

    @Override
    public void payOrder(String orderNo, Long userId) {
        Order order = getOrderByNo(orderNo);
        
        // 权限检查
        checkOrderOwner(order, userId);
        
        // 状态检查
        if (!OrderStatus.PENDING_PAYMENT.equals(order.getStatus())) {
            throw new BusinessException("只有待支付的订单才能支付");
        }

        // 更新订单状态
        order.setStatus(OrderStatus.CHARGING);
        order.setPayTime(LocalDateTime.now());
        order.setUpdateTime(LocalDateTime.now());
        this.updateById(order);
        log.info("订单已支付 | orderNo: {} | userId: {}", orderNo, userId);

        // 更新充电桩状态
        stationFeignClient.updateStationStatus(order.getStationId(), StationStatus.CHARGING);
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
