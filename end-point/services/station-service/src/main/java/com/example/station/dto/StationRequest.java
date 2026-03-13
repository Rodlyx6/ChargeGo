package com.example.station.dto;

import lombok.Data;

/**
 * 新增/修改充电桩请求参数
 */
@Data
public class StationRequest {
    private String snCode;      // 设备编号
    private String address;     // 详细地址
    private Double longitude;   // 经度
    private Double latitude;    // 纬度
}
