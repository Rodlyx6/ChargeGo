package com.example.station.dto;

import lombok.Data;

/**
 * 附近充电桩返回结果（带距离）
 */
@Data
public class NearbyStationVO {
    private Long id;
    private String snCode;
    private Integer status;     // 0空闲 1预约中 2充电中 3故障
    private String address;
    private Double distance;    // 距离（单位：米）
}
