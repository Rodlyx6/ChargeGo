package com.example.common.constant;

/**
 * 充电桩状态常量
 */
public class StationStatus {
    
    /** 空闲 */
    public static final Integer IDLE = 0;
    
    /** 预约中 */
    public static final Integer RESERVED = 1;
    
    /** 充电中 */
    public static final Integer CHARGING = 2;
    
    /** 故障 */
    public static final Integer FAULT = 3;
    
    private StationStatus() {}
    
    /**
     * 获取状态名称
     */
    public static String getName(Integer status) {
        if (status == null) return "未知";
        return switch (status) {
            case 0 -> "空闲";
            case 1 -> "预约中";
            case 2 -> "充电中";
            case 3 -> "故障";
            default -> "未知";
        };
    }
}
