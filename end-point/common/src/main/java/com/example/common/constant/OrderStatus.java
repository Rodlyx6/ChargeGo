package com.example.common.constant;

/**
 * 订单状态常量
 */
public class OrderStatus {
    
    /** 待支付 */
    public static final Integer PENDING_PAYMENT = 0;
    
    /** 充电中 */
    public static final Integer CHARGING = 1;
    
    /** 已完成 */
    public static final Integer COMPLETED = 2;
    
    /** 已取消 */
    public static final Integer CANCELLED = 3;
    
    private OrderStatus() {}
    
    /**
     * 获取状态名称
     */
    public static String getName(Integer status) {
        if (status == null) return "未知";
        return switch (status) {
            case 0 -> "待支付";
            case 1 -> "充电中";
            case 2 -> "已完成";
            case 3 -> "已取消";
            default -> "未知";
        };
    }
}
