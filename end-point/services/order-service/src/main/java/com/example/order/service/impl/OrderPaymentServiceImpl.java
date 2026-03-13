package com.example.order.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.common.result.R;
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
        // 1. 查询订单
        LambdaQueryWrapper<Order> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Order::getOrderNo, orderNo);
        Order order = this.getOne(queryWrapper);

        if (order == null) {
            throw new RuntimeException("订单不存在");
        }

        // 2. 检查订单是否属于当前用户
        if (!order.getUserId().equals(userId)) {
            throw new RuntimeException("无权操作他人的订单");
        }

        // 3. 只有【待支付(0)】状态的订单才能支付
        if (order.getStatus() != 0) {
            throw new RuntimeException("只有待支付的订单才能支付");
        }

        // 4. 更新订单状态为【充电中(1)】，记录支付时间和更新时间
        order.setStatus(1);
        order.setPayTime(LocalDateTime.now());
        order.setUpdateTime(LocalDateTime.now());
        this.updateById(order);
        log.info("订单已支付 | orderNo: {} | userId: {} | stationId: {} | order.status: 1 | pay_time: {} | update_time: {}", 
                orderNo, order.getUserId(), order.getStationId(), order.getPayTime(), order.getUpdateTime());

        // 5. 通过 Feign 将充电桩状态改为【充电中(2)】
        try {
            stationFeignClient.updateStationStatus(order.getStationId(), 2);
            log.info("充电桩已改为充电中 | stationId: {} | station.status: 2", order.getStationId());
        } catch (Exception e) {
            log.error("更新充电桩状态失败 | stationId: {} | error: {}", order.getStationId(), e.getMessage());
            throw new RuntimeException("更新充电桩状态失败");
        }
    }
}
