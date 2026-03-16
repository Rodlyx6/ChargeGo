package com.example.order.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.common.enums.ChargeTypeEnum;
import com.example.common.enums.OrderStatusEnum;
import com.example.common.exception.BusinessException;
import com.example.order.entity.Order;
import com.example.order.mapper.OrderMapper;
import com.example.order.model.dto.ChargeOrderCompleteDTO;
import com.example.order.model.dto.ChargeOrderCreateDTO;
import com.example.order.model.dto.ChargeOrderPayDTO;
import com.example.order.model.vo.ChargeOrderVO;
import com.example.order.service.ChargeOrderService;
import com.example.order.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * 充电订单计算服务实现
 * 职责：处理充电订单的计费、支付、完成等逻辑
 */
@Slf4j
@Service
public class ChargeOrderServiceImpl implements ChargeOrderService {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderMapper orderMapper;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public String createChargeOrder(Long userId, ChargeOrderCreateDTO request) {
        log.info("💡 创建充电订单 | userId: {} | chargeType: {} | chargeTime: {}分钟", 
                userId, request.getChargeType(), request.getChargeTime());

        // 验证充电类型
        ChargeTypeEnum chargeType = ChargeTypeEnum.getByCode(request.getChargeType());
        if (chargeType == null) {
            throw new BusinessException("充电类型不存在");
        }

        // 验证充电时长
        if (request.getChargeTime() == null || request.getChargeTime() <= 0) {
            throw new BusinessException("充电时长必须大于0");
        }

        // 计算预期金额
        Double expectedAmount = chargeType.calculateAmount(request.getChargeTime());
        log.info("💰 预期金额计算 | chargeType: {} | chargeTime: {}分钟 | expectedAmount: {}元", 
                chargeType.getDesc(), request.getChargeTime(), expectedAmount);

        // 调用原有的创建订单逻辑
        Long stationId = Long.parseLong(request.getStationId());
        String orderNo = orderService.createOrder(userId, stationId);

        // 这里可以将 chargeType、chargeTime、expectedAmount 存储到 Redis 或其他地方
        // 因为不修改数据库，所以使用 Redis 缓存
        String chargeOrderKey = "charge:order:" + orderNo;
        // TODO: 存储到 Redis（需要注入 RedisTemplate）

        return orderNo;
    }

    @Override
    public void payChargeOrder(Long userId, ChargeOrderPayDTO request) {
        log.info("💳 支付充电订单 | userId: {} | orderNo: {} | actualAmount: {}元", 
                userId, request.getOrderNo(), request.getActualAmount());

        // 验证金额
        if (request.getActualAmount() == null || request.getActualAmount() <= 0) {
            throw new BusinessException("支付金额必须大于0");
        }

        // 调用原有的支付逻辑
        orderService.payOrder(request.getOrderNo(), userId);

        log.info("✅ 支付成功 | orderNo: {}", request.getOrderNo());
    }

    @Override
    public ChargeOrderVO completeChargeOrder(Long userId, ChargeOrderCompleteDTO request) {
        log.info("⏹️ 完成充电订单 | userId: {} | orderNo: {} | actualChargeTime: {}分钟", 
                userId, request.getOrderNo(), request.getActualChargeTime());

        // 获取订单
        Order order = getOrderByNo(request.getOrderNo());
        checkOrderOwner(order, userId);

        // 验证订单状态
        if (!OrderStatusEnum.CHARGING.getCode().equals(order.getStatus())) {
            throw new BusinessException("只有充电中的订单才能完成");
        }

        // 验证实际充电时长
        if (request.getActualChargeTime() == null || request.getActualChargeTime() <= 0) {
            throw new BusinessException("实际充电时长必须大于0");
        }

        // 从缓存获取预期金额和充电类型
        String chargeOrderKey = "charge:order:" + request.getOrderNo();
        // TODO: 从 Redis 获取 chargeType 和 expectedAmount

        // 这里假设已经获取到了 chargeType 和 expectedAmount
        // 实际应该从 Redis 获取
        Integer chargeType = 1;  // 示例
        Double expectedAmount = 10.0;  // 示例

        // 计算实际金额
        ChargeTypeEnum chargeTypeEnum = ChargeTypeEnum.getByCode(chargeType);
        Double actualAmount = chargeTypeEnum.calculateAmount(request.getActualChargeTime());

        // 计算退款金额
        Double refundAmount = expectedAmount - actualAmount;
        log.info("💰 订单完成计算 | expectedAmount: {}元 | actualAmount: {}元 | refundAmount: {}元", 
                expectedAmount, actualAmount, refundAmount);

        // 完成订单
        orderService.cancelOrder(request.getOrderNo(), userId);

        // 构建返回 VO
        return buildChargeOrderVO(order, chargeType, expectedAmount, request.getActualChargeTime(), actualAmount, refundAmount);
    }

    @Override
    public ChargeOrderVO getChargeOrderDetail(Long userId, String orderNo) {
        log.info("🔍 查询充电订单详情 | userId: {} | orderNo: {}", userId, orderNo);

        Order order = getOrderByNo(orderNo);
        checkOrderOwner(order, userId);

        // 从缓存获取充电信息
        String chargeOrderKey = "charge:order:" + orderNo;
        // TODO: 从 Redis 获取 chargeType、chargeTime、expectedAmount 等

        // 示例数据
        Integer chargeType = 1;
        Integer chargeTime = 60;
        Double expectedAmount = 2.0;

        return buildChargeOrderVO(order, chargeType, expectedAmount, chargeTime, null, null);
    }

    @Override
    public void autoCompleteTimeoutOrder(String orderNo) {
        log.info("⏱️ 自动完成超时订单 | orderNo: {}", orderNo);

        Order order = getOrderByNo(orderNo);

        // 如果订单还在充电中，则自动完成
        if (OrderStatusEnum.CHARGING.getCode().equals(order.getStatus())) {
            // 计算从支付时间到现在的充电时长
            LocalDateTime payTime = order.getPayTime();
            LocalDateTime now = LocalDateTime.now();
            long minutes = java.time.temporal.ChronoUnit.MINUTES.between(payTime, now);

            log.info("⏱️ 自动完成 | orderNo: {} | chargeTime: {}分钟", orderNo, minutes);

            // 完成订单（不需要用户操作）
            // TODO: 直接更新订单状态为已完成
        }
    }

    // ==================== 私有方法 ====================

    /**
     * 根据订单号查询订单
     */
    private Order getOrderByNo(String orderNo) {
        LambdaQueryWrapper<Order> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Order::getOrderNo, orderNo);
        Order order = orderMapper.selectOne(queryWrapper);
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

    /**
     * 构建充电订单 VO
     */
    private ChargeOrderVO buildChargeOrderVO(Order order, Integer chargeType, Double expectedAmount, 
                                             Integer chargeTime, Double actualAmount, Double refundAmount) {
        ChargeOrderVO vo = new ChargeOrderVO();
        vo.setOrderNo(order.getOrderNo());
        vo.setUserId(String.valueOf(order.getUserId()));
        vo.setStationId(String.valueOf(order.getStationId()));
        vo.setChargeType(chargeType);
        vo.setChargeTypeDesc(ChargeTypeEnum.getByCode(chargeType).getDesc());
        vo.setChargeTime(chargeTime);
        vo.setExpectedAmount(expectedAmount);
        vo.setStatus(order.getStatus());
        vo.setStatusDesc(com.example.common.enums.OrderStatusEnum.getDesc(order.getStatus()));
        vo.setActualChargeTime(chargeTime);
        vo.setActualAmount(actualAmount);
        vo.setRefundAmount(refundAmount);
        vo.setCreateTime(order.getCreateTime().format(DATE_FORMATTER));
        vo.setPayTime(order.getPayTime() != null ? order.getPayTime().format(DATE_FORMATTER) : null);
        vo.setCompleteTime(order.getUpdateTime().format(DATE_FORMATTER));
        return vo;
    }
}
