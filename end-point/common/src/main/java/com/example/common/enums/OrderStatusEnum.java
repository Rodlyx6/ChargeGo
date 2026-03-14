package com.example.common.enums;

import lombok.Getter;

/**
 * 订单状态枚举
 */
@Getter
public enum OrderStatusEnum {
    
    /** 待支付 */
    PENDING_PAYMENT(0, "待支付"),
    
    /** 充电中 */
    CHARGING(1, "充电中"),
    
    /** 已完成 */
    COMPLETED(2, "已完成"),
    
    /** 已取消 */
    CANCELLED(3, "已取消");
    
    private final Integer code;
    private final String desc;
    
    OrderStatusEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }
    
    /**
     * 根据 code 获取枚举
     */
    public static OrderStatusEnum getByCode(Integer code) {
        if (code == null) return null;
        for (OrderStatusEnum status : values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return null;
    }
    
    /**
     * 获取状态描述
     */
    public static String getDesc(Integer code) {
        OrderStatusEnum status = getByCode(code);
        return status != null ? status.getDesc() : "未知";
    }
}
