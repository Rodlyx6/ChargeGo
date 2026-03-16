package com.example.station.model.query;

import lombok.Data;

/**
 * 附近充电桩查询条件
 */
@Data
public class NearbyStationQuery {
    private Double longitude;     // 经度
    private Double latitude;      // 纬度
    private Double radiusMeters; // 搜索半径（米）
}
