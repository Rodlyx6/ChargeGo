package com.example.order.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.common.enums.OrderStatusEnum;
import com.example.common.exception.BusinessException;
import com.example.order.entity.Order;
import com.example.order.mapper.OrderMapper;
import com.example.order.model.vo.OrderVO;
import com.example.order.service.OrderAdminService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 管理员订单管理服务实现
 * 职责：提供管理员端的订单查询、统计功能
 */
@Slf4j
@Service
public class OrderAdminServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderAdminService {

    @Override
    public Page<OrderVO> getOrderPage(Integer pageNum, Integer pageSize, Integer status, Long userId) {
        log.info("📋 管理员查询订单列表 | pageNum: {} | pageSize: {} | status: {} | userId: {}", 
                pageNum, pageSize, status, userId);

        // 参数验证
        validatePageParams(pageNum, pageSize);

        Page<Order> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Order> queryWrapper = buildOrderQueryWrapper(status, userId);

        Page<Order> result = this.page(page, queryWrapper);

        // 转换为 VO
        Page<OrderVO> voPage = new Page<>(result.getCurrent(), result.getSize(), result.getTotal());
        List<OrderVO> voList = result.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        voPage.setRecords(voList);

        log.info("✅ 查询成功 | 总数: {} | 当前页: {}", result.getTotal(), result.getCurrent());
        return voPage;
    }

    @Override
    public OrderVO getOrderDetail(String orderNo) {
        log.info("🔍 管理员查询订单详情 | orderNo: {}", orderNo);

        // 参数验证
        if (orderNo == null || orderNo.trim().isEmpty()) {
            throw new BusinessException("订单号不能为空");
        }

        LambdaQueryWrapper<Order> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Order::getOrderNo, orderNo);
        Order order = this.getOne(queryWrapper);

        if (order == null) {
            log.warn("❌ 订单不存在 | orderNo: {}", orderNo);
            throw new BusinessException(404, "订单不存在");
        }

        log.info("✅ 查询成功 | orderNo: {}", orderNo);
        return convertToVO(order);
    }

    @Override
    public Map<String, Object> getOrderStats() {
        log.info("📊 管理员查询订单统计");

        Map<String, Object> stats = new HashMap<>();

        try {
            // 总订单数
            long totalOrders = this.count();
            stats.put("totalOrders", totalOrders);

            // 各状态订单数
            long pendingPayment = countByStatus(OrderStatusEnum.PENDING_PAYMENT.getCode());
            stats.put("pendingPayment", pendingPayment);

            long charging = countByStatus(OrderStatusEnum.CHARGING.getCode());
            stats.put("charging", charging);

            long completed = countByStatus(OrderStatusEnum.COMPLETED.getCode());
            stats.put("completed", completed);

            long cancelled = countByStatus(OrderStatusEnum.CANCELLED.getCode());
            stats.put("cancelled", cancelled);

            log.info("✅ 统计成功 | 总订单: {} | 待支付: {} | 充电中: {} | 已完成: {} | 已取消: {}", 
                    totalOrders, pendingPayment, charging, completed, cancelled);
        } catch (Exception e) {
            log.error("❌ 统计失败", e);
            throw new BusinessException("订单统计失败");
        }

        return stats;
    }

    // ==================== 私有方法 ====================

    /**
     * 验证分页参数
     */
    private void validatePageParams(Integer pageNum, Integer pageSize) {
        if (pageNum == null || pageNum < 1) {
            throw new BusinessException("页码必须大于0");
        }
        if (pageSize == null || pageSize < 1 || pageSize > 100) {
            throw new BusinessException("每页数量必须在1-100之间");
        }
    }

    /**
     * 构建订单查询条件
     */
    private LambdaQueryWrapper<Order> buildOrderQueryWrapper(Integer status, Long userId) {
        LambdaQueryWrapper<Order> queryWrapper = new LambdaQueryWrapper<>();

        // 按状态筛选
        if (status != null && status >= 0 && status <= 3) {
            queryWrapper.eq(Order::getStatus, status);
        }

        // 按用户ID筛选
        if (userId != null && userId > 0) {
            queryWrapper.eq(Order::getUserId, userId);
        }

        // 按创建时间倒序
        queryWrapper.orderByDesc(Order::getCreateTime);

        return queryWrapper;
    }

    /**
     * 按状态统计订单数
     */
    private long countByStatus(Integer status) {
        LambdaQueryWrapper<Order> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Order::getStatus, status);
        return this.count(queryWrapper);
    }

    /**
     * 转换为 VO
     */
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
