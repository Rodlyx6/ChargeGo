package com.example.order.enums;

import lombok.Getter;

/**
 * 充电类型枚举
 */
@Getter
public enum ChargeTypeEnum {
    
    /** 快充：10元/小时 */
    FAST(1, "快充", 10.0),
    
    /** 普通充电：5元/小时 */
    NORMAL(2, "普通充电", 5.0),
    
    /** 慢充：2元/小时 */
    SLOW(3, "慢充", 2.0);
    
    private final Integer code;
    private final String desc;
    private final Double pricePerHour;
    
    ChargeTypeEnum(Integer code, String desc, Double pricePerHour) {
        this.code = code;
        this.desc = desc;
        this.pricePerHour = pricePerHour;
    }
    
    /**
     * 根据 code 获取枚举
     */
    public static ChargeTypeEnum getByCode(Integer code) {
        if (code == null) return null;
        for (ChargeTypeEnum type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return null;
    }
    
    /**
     * 计算充电费用
     * @param chargeHours 充电小时数
     * @return 费用（元）
     */
    public Double calculatePrice(Integer chargeHours) {
        if (chargeHours == null || chargeHours <= 0) {
            return 0.0;
        }
        return this.pricePerHour * chargeHours;
    }
}
