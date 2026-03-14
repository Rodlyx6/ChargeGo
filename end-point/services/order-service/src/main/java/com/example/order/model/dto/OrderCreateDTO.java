package com.example.order.model.dto;

import lombok.Data;

/**
 * 创建订单 DTO
 */
@Data
public class OrderCreateDTO {
    /**
     * 充电桩 ID
     * 使用 String 类型接收，避免前端 JavaScript 精度丢失
     */
    private String stationId;
}
