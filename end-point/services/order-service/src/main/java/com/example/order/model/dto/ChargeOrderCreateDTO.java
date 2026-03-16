package com.example.order.model.dto;

import lombok.Data;

/**
 * 创建充电订单请求 DTO
 */
@Data
public class ChargeOrderCreateDTO {
    
    /** 充电桩ID */
    private String stationId;
    
    /** 充电类型：1快充 2慢充 */
    private Integer chargeType;
    
    /** 充电时长（分钟） */
    private Integer chargeTime;
}
