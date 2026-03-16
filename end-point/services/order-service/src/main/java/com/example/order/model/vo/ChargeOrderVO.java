package com.example.order.model.vo;

import lombok.Data;

/**
 * 充电订单详情 VO
 */
@Data
public class ChargeOrderVO {
    
    /** 订单号 */
    private String orderNo;
    
    /** 用户ID */
    private String userId;
    
    /** 充电桩ID */
    private String stationId;
    
    /** 充电类型：1快充 2慢充 */
    private Integer chargeType;
    
    /** 充电类型描述 */
    private String chargeTypeDesc;
    
    /** 预期充电时长（分钟） */
    private Integer chargeTime;
    
    /** 预期金额（元） */
    private Double expectedAmount;
    
    /** 订单状态：0待支付 1充电中 2已完成 3已取消 */
    private Integer status;
    
    /** 订单状态描述 */
    private String statusDesc;
    
    /** 实际充电时长（分钟，充电完成后才有） */
    private Integer actualChargeTime;
    
    /** 实际金额（元，充电完成后才有） */
    private Double actualAmount;
    
    /** 退款金额（元，预期金额 - 实际金额） */
    private Double refundAmount;
    
    /** 创建时间 */
    private String createTime;
    
    /** 支付时间 */
    private String payTime;
    
    /** 完成时间 */
    private String completeTime;
}
