package com.example.station.model.vo;

import lombok.Data;

/**
 * 充电桩列表 VO
 * 用于附近充电桩搜索结果
 */
@Data
public class StationListVO {
    private String id;
    private String snCode;
    private Integer status;
    private String statusDesc;
    private String address;
    private Double distance;  // 距离（米）
}
