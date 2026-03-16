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
    /**
     * 充电类型
     * 1 = 快充（10元/小时）
     * 2 = 普通充电（5元/小时）
     * 3 = 慢充（2元/小时）
     */
    private Integer chargeType;
    
    /**
     * 充电类型描述
     * 例如："快充"、"普通充电"、"慢充"
     */
    private String chargeTypeDesc;
    
    /**
     * 预约充电时间（小时）
     */
    private Integer chargeTime;
    
    /**
     * 预期金额（元）
     * 计算公式：chargeType对应的单价 × chargeTime
     */
    private Double expectedAmount;
    
    /**
     * 实际充电金额（元）
     * 只在订单完成时有值
     * 计算公式：chargeType对应的单价 × actualChargeTime
     */
    private Double actualAmount;
    
    /**
     * 退款金额（元）
     * 只在订单完成时有值
     * 计算公式：expectedAmount - actualAmount
     */
    private Double refundAmount;
    
    /**
     * 实际充电时间（小时，保留2位小数）
     * 只在订单完成时有值
     * 计算公式：(取消时间 - 支付时间) / 60分钟
     */
    private Double actualChargeTime;
    
    private LocalDateTime payTime;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
