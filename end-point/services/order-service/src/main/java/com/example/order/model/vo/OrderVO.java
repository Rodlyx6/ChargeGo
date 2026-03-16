package com.example.order.model.vo;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 订单详情 VO
 */
@Data
public class OrderVO {
    private String orderNo;
    private String userId;
    private String stationId;
    private Integer status;
    private String statusDesc;
    
    // 充电相关
    private Integer chargeType;
    private String chargeTypeDesc;
    private Integer chargeTime;        // 预约充电时间（小时）
    private Double expectedAmount;     // 预期金额
    private Double actualAmount;       // 实际金额
    private Double refundAmount;       // 退款金额
    private Double actualChargeTime;   // 实际充电时间（小时，保留2位小数）
    
    private LocalDateTime payTime;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
