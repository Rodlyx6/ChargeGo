package com.example.order.model.dto;

import lombok.Data;

/**
 * 支付充电订单请求 DTO
 */
@Data
public class ChargeOrderPayDTO {
    
    /** 订单号 */
    private String orderNo;
    
    /** 实际支付金额（用户支付的金额） */
    private Double actualAmount;
}
