package com.example.common.enums;

import lombok.Getter;

/**
 * 充电类型枚举
 * 定义不同的充电类型和对应的价格
 */
@Getter
public enum ChargeTypeEnum {
    
    /** 快充：2元/分钟 */
    FAST(1, "快充", 2.0),
    
    /** 普通充电：1元/分钟 */
    NORMAL(2, "普通充电", 1.0),
    
    /** 慢充：0.5元/分钟 */
    SLOW(3, "慢充", 0.5);
    
    private final Integer code;
    private final String desc;
    private final Double pricePerMinute;
    
    ChargeTypeEnum(Integer code, String desc, Double pricePerMinute) {
        this.code = code;
        this.desc = desc;
        this.pricePerMinute = pricePerMinute;
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
     * 获取充电类型描述
     */
    public static String getDesc(Integer code) {
        ChargeTypeEnum type = getByCode(code);
        return type != null ? type.getDesc() : "未知";
    }
    
    /**
     * 获取单位价格（元/分钟）
     */
    public static Double getPricePerMinute(Integer code) {
        ChargeTypeEnum type = getByCode(code);
        return type != null ? type.getPricePerMinute() : 0.0;
    }
}
