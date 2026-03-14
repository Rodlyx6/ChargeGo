package com.example.order.model.dto;

import lombok.Data;

/**
 * 取消订单 DTO
 */
@Data
public class OrderCancelDTO {
    /**
     * 订单号
     */
    private String orderNo;
}
