package com.example.order.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.order.entity.Order;
import com.example.order.model.vo.OrderVO;

import java.util.Map;

/**
 * 管理员订单管理服务接口
 * 职责：提供管理员端的订单查询、统计功能
 * 与 OrderService 分离，遵循单一职责原则
 */
public interface OrderAdminService {

    /**
     * 分页查询订单列表
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @param status 订单状态（可选）
     * @param userId 用户ID（可选）
     * @return 分页订单数据
     */
    Page<OrderVO> getOrderPage(Integer pageNum, Integer pageSize, Integer status, Long userId);

    /**
     * 获取订单详情
     * @param orderNo 订单号
     * @return 订单详情
     */
    OrderVO getOrderDetail(String orderNo);

    /**
     * 获取订单统计数据
     * @return 统计数据（总数、各状态数）
     */
    Map<String, Object> getOrderStats();
}
