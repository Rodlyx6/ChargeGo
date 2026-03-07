package com.example.order.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigInteger;
import java.time.LocalDateTime;

@Data
@TableName("order_info")
public class Order {
   @TableId(value = "id", type=IdType.ASSIGN_ID)
   private Long id;
   // 订单号
   @TableField("order_no")
   private String orderNo;
   @TableField("user_id")
   private Long userId;
   @TableField("station_id")
   private Long stationId;

   // 订单状态 0:未支付 1:支付成功 2:支付失败
   @TableField("status")
   private Integer status;

   // 支付时间
   @TableField("pay_time")
   private LocalDateTime payTime;

   // 下单时间
   @TableField(value = "create_time" , fill = FieldFill.INSERT)
   private LocalDateTime createTime;
   @TableField(value = "update_time" , fill = FieldFill.INSERT_UPDATE)
   private LocalDateTime updateTime;

}
