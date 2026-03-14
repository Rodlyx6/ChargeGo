package com.example.station.model.vo;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 充电桩详情 VO
 */
@Data
public class StationVO {
    private String id;
    private String snCode;
    private Integer status;
    private String statusDesc;
    private String address;
    private Double longitude;
    private Double latitude;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
