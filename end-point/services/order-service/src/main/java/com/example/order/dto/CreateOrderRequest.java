package com.example.order.dto;

import lombok.Data;

/**
 * 下单请求参数
 */
@Data
public class CreateOrderRequest {
    // 要预约的充电桩 ID
    private Long stationId;
}
