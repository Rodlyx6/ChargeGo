package com.example.station.dto;

import lombok.Data;

/**
 * 附近充电桩查询请求参数
 */
@Data
public class NearbySearchRequest {
    // 当前位置经度
    private Double longitude;
    // 当前位置纬度
    private Double latitude;
    // 搜索半径（单位：米，默认 5000 米）
    private Double radiusMeters = 5000.0;
}
