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
    
    /**
     * 充电类型
     * 1 = 快充（10元/小时）
     * 2 = 普通充电（5元/小时）
     * 3 = 慢充（2元/小时）
     */
    private Integer chargeType;
    
    /**
     * 充电时间（小时）
     * 例如：2 表示预约充电 2 小时
     */
    private Integer chargeTime;
}
