package com.example.station.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.station.entity.Station;
import com.example.station.model.dto.StationCreateDTO;
import com.example.station.model.dto.StationUpdateDTO;
import com.example.station.model.vo.StationListVO;
import com.example.station.model.vo.StationVO;

import java.util.List;

/**
 * 充电桩服务接口（统一）
 * 整合了普通用户和管理员的所有充电桩相关功能
 */
public interface StationService extends IService<Station> {

    // ==================== 普通用户接口 ====================

    /**
     * 查询附近的充电桩
     * @param longitude 当前位置经度
     * @param latitude  当前位置纬度
     * @param radiusMeters 搜索半径（米）
     * @return 按距离升序排列的充电桩列表
     */
    List<StationListVO> getNearbyStations(Double longitude, Double latitude, Double radiusMeters);

    /**
     * 修改充电桩状态（供 order-service 通过 Feign 调用）
     * @param stationId 充电桩ID
     * @param status    目标状态（0空闲 1预约中 2充电中 3故障）
     */
    void updateStationStatus(Long stationId, Integer status);

    // ==================== 管理员接口 ====================

    /**
     * 查询所有充电桩
     */
    List<StationVO> listAllStations();

    /**
     * 新增充电桩
     */
    void addStation(StationCreateDTO request);

    /**
     * 修改充电桩
     */
    void updateStation(Long id, StationUpdateDTO request);

    /**
     * 删除充电桩
     */
    void deleteStation(Long id);

    /**
     * 根据ID查询充电桩
     */
    StationVO getStationById(Long id);
}
