package com.example.station.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.station.dto.NearbyStationVO;
import com.example.station.entity.Station;

import java.util.List;

public interface StationService extends IService<Station> {

    /**
     * 查询附近的充电桩
     * @param longitude 当前位置经度
     * @param latitude  当前位置纬度
     * @param radiusMeters 搜索半径（米）
     * @return 按距离升序排列的充电桩列表
     */
    List<NearbyStationVO> getNearbyStations(Double longitude, Double latitude, Double radiusMeters);

    /**
     * 修改充电桩状态（供 order-service 通过 Feign 调用）
     * @param stationId 充电桩ID
     * @param status    目标状态（0空闲 1预约中 2充电中 3故障）
     */
    void updateStationStatus(Long stationId, Integer status);
}
