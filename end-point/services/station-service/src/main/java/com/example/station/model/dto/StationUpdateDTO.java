package com.example.station.model.dto;

import lombok.Data;

/**
 * 充电桩更新 DTO
 */
@Data
public class StationUpdateDTO {
    private String snCode;      // 设备编号
    private String address;     // 详细地址
    private Double longitude;   // 经度
    private Double latitude;    // 纬度
}
