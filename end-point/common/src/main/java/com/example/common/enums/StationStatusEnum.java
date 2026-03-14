package com.example.common.enums;

import lombok.Getter;

/**
 * 充电桩状态枚举
 */
@Getter
public enum StationStatusEnum {
    
    /** 空闲 */
    IDLE(0, "空闲"),
    
    /** 预约中 */
    RESERVED(1, "预约中"),
    
    /** 充电中 */
    CHARGING(2, "充电中"),
    
    /** 故障 */
    FAULT(3, "故障");
    
    private final Integer code;
    private final String desc;
    
    StationStatusEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }
    
    /**
     * 根据 code 获取枚举
     */
    public static StationStatusEnum getByCode(Integer code) {
        if (code == null) return null;
        for (StationStatusEnum status : values()) {
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
        StationStatusEnum status = getByCode(code);
        return status != null ? status.getDesc() : "未知";
    }
}
