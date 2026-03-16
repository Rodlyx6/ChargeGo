package com.example.order.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("order_info")
public class Order {
   @TableId(value = "id", type=IdType.ASSIGN_ID)
   private Long id;
   
   @TableField("order_no")
   private String orderNo;
   
   @TableField("user_id")
   private Long userId;
   
   @TableField("station_id")
   private Long stationId;

   // 订单状态 0:待支付 1:充电中 2:已完成 3:已取消
   @TableField("status")
   private Integer status;

   // 充电类型 1:快充 2:普通充电 3:慢充
   @TableField("charge_type")
   private Integer chargeType;
   
   // 充电时间（小时）
   @TableField("charge_time")
   private Integer chargeTime;
   
   // 预期金额
   @TableField("expected_amount")
   private Double expectedAmount;
   
   // 实际充电金额
   @TableField("actual_amount")
   private Double actualAmount;
   
   // 退款金额
   @TableField("refund_amount")
   private Double refundAmount;

   // 支付时间
   @TableField("pay_time")
   private LocalDateTime payTime;

   // 下单时间
   @TableField(value = "create_time" , fill = FieldFill.INSERT)
   private LocalDateTime createTime;
   
   @TableField(value = "update_time" , fill = FieldFill.INSERT_UPDATE)
   private LocalDateTime updateTime;
}

