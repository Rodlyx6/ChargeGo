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

    /**
     * 充电类型：1-快充(2元/分钟) 2-普通充电(1元/分钟) 3-慢充(0.5元/分钟)
     */
    @TableField("charge_type")
    private Integer chargeType;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    // ==================== 查询结果字段（非数据库字段） ====================

    /**
     * 距离（米）- 查询时计算，不存储在数据库
     */
    @TableField(exist = false)
    private Double distance;

    /**
     * 经度 - 从 location 字段解析
     */
    @TableField(exist = false)
    private Double longitude;

    /**
     * 纬度 - 从 location 字段解析
     */
    @TableField(exist = false)
    private Double latitude;
}
