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
    private LocalDateTime payTime;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
