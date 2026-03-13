package com.example.order.dto;

import lombok.Data;

/**
 * 支付请求参数
 */
@Data
public class PaymentRequest {
    private String orderNo;  // 订单号
}
