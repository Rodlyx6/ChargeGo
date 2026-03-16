package com.example.order.model.dto;

import lombok.Data;

/**
 * 完成充电订单请求 DTO
 */
@Data
public class ChargeOrderCompleteDTO {
    
    /** 订单号 */
    private String orderNo;
    
    /** 实际充电时长（分钟） */
    private Integer actualChargeTime;
}
