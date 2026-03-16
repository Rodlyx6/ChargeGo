package com.example.common.enums;

import lombok.Getter;

/**
 * 充电类型枚举
 */
@Getter
public enum ChargeTypeEnum {
    
    /** 快充 */
    FAST(1, "快充", 2.0),
    
    /** 慢充 */
    SLOW(2, "慢充", 1.0);
    
    private final Integer code;
    private final String desc;
    private final Double pricePerHour;  // 每小时价格（元）
    
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
     * 计算预期金额
     * @param chargeTime 充电时长（分钟）
     * @return 预期金额（元）
     */
    public Double calculateAmount(Integer chargeTime) {
        if (chargeTime == null || chargeTime <= 0) {
            return 0.0;
        }
        // 转换为小时，向上取整
        double hours = Math.ceil(chargeTime / 60.0);
        return pricePerHour * hours;
    }
}
