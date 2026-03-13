package com.example.order.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
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

        // 3. 只有【待支付(0)】状态的订单才能取消支付
        if (order.getStatus() != 0) {
            throw new RuntimeException("只有待支付的订单才能取消");
        }

        // 4. 将订单状态改为【已取消(3)】，更新 update_time
        order.setStatus(3);
        order.setUpdateTime(LocalDateTime.now());
        this.updateById(order);
        log.info("订单已取消支付 | orderNo: {} | userId: {} | order.status: 3 | update_time: {}", 
                orderNo, order.getUserId(), order.getUpdateTime());

        // 5. 通过 Feign 将充电桩状态改回【空闲(0)】
        try {
            stationFeignClient.updateStationStatus(order.getStationId(), 0);
            log.info("充电桩已释放为空闲 | stationId: {} | station.status: 0", order.getStationId());
        } catch (Exception e) {
            log.error("释放充电桩失败 | stationId: {} | error: {}", order.getStationId(), e.getMessage());
            throw new RuntimeException("释放充电桩失败");
        }
    }

    @Override
    public void cancelOrder(String orderNo, Long userId) {
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

        // 3. 只有【充电中(1)】状态的订单才能取消
        if (order.getStatus() != 1) {
            throw new RuntimeException("只有充电中的订单才能取消");
        }

        // 4. 将订单状态改为【已完成(2)】，更新 update_time（用于计算使用时长）
        order.setStatus(2);
        order.setUpdateTime(LocalDateTime.now());
        this.updateById(order);
        log.info("订单已取消 | orderNo: {} | userId: {} | order.status: 2 | update_time: {} | 使用时长可根据 create_time 和 update_time 计算", 
                orderNo, order.getUserId(), order.getUpdateTime());

        // 5. 通过 Feign 将充电桩状态改回【空闲(0)】
        try {
            stationFeignClient.updateStationStatus(order.getStationId(), 0);
            log.info("充电桩已释放为空闲 | stationId: {} | station.status: 0", order.getStationId());
        } catch (Exception e) {
            log.error("释放充电桩失败 | stationId: {} | error: {}", order.getStationId(), e.getMessage());
            throw new RuntimeException("释放充电桩失败");
        }
    }

    @Override
    public void cancelTimeoutOrder(String orderNo) {
        // 1. 查询订单
        LambdaQueryWrapper<Order> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Order::getOrderNo, orderNo);
        Order order = this.getOne(queryWrapper);

        if (order == null) {
            log.warn("超时取消：订单不存在 | orderNo: {}", orderNo);
            return;
        }

        // 2. 只有【待支付(0)】状态的订单才需要取消
        if (order.getStatus() != 0) {
            log.info("超时取消：订单已处理，无需取消 | orderNo: {} | order.status: {}", orderNo, order.getStatus());
            return;
        }

        // 3. 将订单状态改为【已取消(3)】，更新 update_time
        order.setStatus(3);
        order.setUpdateTime(LocalDateTime.now());
        this.updateById(order);
        log.info("订单已超时取消 | orderNo: {} | order.status: 3 | update_time: {}", orderNo, order.getUpdateTime());

        // 4. 通过 Feign 将充电桩状态改回【空闲(0)】
        try {
            stationFeignClient.updateStationStatus(order.getStationId(), 0);
            log.info("充电桩已释放为空闲 | stationId: {} | station.status: 0", order.getStationId());
        } catch (Exception e) {
            log.error("释放充电桩失败 | stationId: {} | error: {}", order.getStationId(), e.getMessage());
        }
    }
}
