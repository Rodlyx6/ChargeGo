package com.example.station.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.station.dto.StationRequest;
import com.example.station.entity.Station;

import java.util.List;

/**
 * 管理员充电桩管理服务接口
 */
public interface AdminStationService extends IService<Station> {

    /**
     * 查询所有充电桩
     */
    List<Station> listAllStations();

    /**
     * 新增充电桩
     */
    void addStation(StationRequest request);

    /**
     * 修改充电桩
     */
    void updateStation(Long id, StationRequest request);

    /**
     * 删除充电桩
     */
    void deleteStation(Long id);

    /**
     * 根据ID查询充电桩
     */
    Station getStationById(Long id);
}
