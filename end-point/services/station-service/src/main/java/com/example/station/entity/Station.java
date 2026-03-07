package com.example.station.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("station")
public class Station {
    @TableId(value = "id", type= IdType.ASSIGN_ID)
    private Long id;

    // 设备序列号 (VARCHAR(50))
    @TableField("sn_code")
    private String snCode;

    // 状态：0-空闲 1-使用中 2-故障等 (TINYINT(1))
    @TableField("status")
    private Integer status;

    @TableField("address")
    private String address;

    /**
     * 地理位置坐标 (POINT 类型)
     * MySQL 的 POINT 类型可以用 String 存储，格式为 "POINT(经度 纬度)"
     * 或者使用 JTS 库的 Point 对象
     */
    @TableField("location")
    private String location;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
