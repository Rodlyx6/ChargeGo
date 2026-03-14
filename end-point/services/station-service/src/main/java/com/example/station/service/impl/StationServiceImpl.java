package com.example.station.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.common.enums.StationStatusEnum;
import com.example.common.exception.BusinessException;
import com.example.station.entity.Station;
import com.example.station.mapper.StationMapper;
import com.example.station.model.dto.StationCreateDTO;
import com.example.station.model.dto.StationUpdateDTO;
import com.example.station.model.vo.StationListVO;
import com.example.station.model.vo.StationVO;
import com.example.station.service.StationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 充电桩服务实现（统一）
 * 整合了普通用户和管理员的所有充电桩相关功能
 */
@Slf4j
@Service
public class StationServiceImpl extends ServiceImpl<StationMapper, Station> implements StationService {

    @Autowired
    private StationMapper stationMapper;

    // ==================== 普通用户接口 ====================

    @Override
    public List<StationListVO> getNearbyStations(Double longitude, Double latitude, Double radiusMeters) {
        log.info("查询附近充电桩 | 经度: {} | 纬度: {} | 半径: {}米", longitude, latitude, radiusMeters);
        
        List<Station> stations = stationMapper.findNearbyStations(longitude, latitude, radiusMeters);
        log.info("查询结果: 共找到 {} 个充电桩", stations.size());
        
        return stations.stream().map(this::convertToListVO).collect(Collectors.toList());
    }

    @Override
    public void updateStationStatus(Long stationId, Integer status) {
        Station station = this.getById(stationId);
        if (station == null) {
            throw new BusinessException("充电桩不存在");
        }

        // 只更新 status 字段，避免 GEOMETRY 字段问题
        LambdaUpdateWrapper<Station> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Station::getId, stationId).set(Station::getStatus, status);
        this.update(updateWrapper);

        log.info("充电桩状态更新 | stationId: {} | 新状态: {}", stationId, status);
    }

    // ==================== 管理员接口 ====================

    @Override
    public List<StationVO> listAllStations() {
        List<Station> stations = stationMapper.selectAllWithLocationText();
        return stations.stream().map(this::convertToVO).collect(Collectors.toList());
    }

    @Override
    public void addStation(StationCreateDTO request) {
        checkSnCodeExists(request.getSnCode(), null);

        Station station = new Station();
        station.setSnCode(request.getSnCode());
        station.setAddress(request.getAddress());
        station.setLocation(buildLocation(request.getLongitude(), request.getLatitude()));
        station.setStatus(StationStatusEnum.IDLE.getCode());
        station.setCreateTime(LocalDateTime.now());
        station.setUpdateTime(LocalDateTime.now());

        stationMapper.insertWithGeometry(station);
        log.info("充电桩已新增 | snCode: {}", request.getSnCode());
    }

    @Override
    public void updateStation(Long id, StationUpdateDTO request) {
        Station station = this.getById(id);
        if (station == null) {
            throw new BusinessException("充电桩不存在");
        }
        
        checkStationIdle(station);
        checkSnCodeExists(request.getSnCode(), id);

        station.setSnCode(request.getSnCode());
        station.setAddress(request.getAddress());
        station.setLocation(buildLocation(request.getLongitude(), request.getLatitude()));
        station.setUpdateTime(LocalDateTime.now());

        stationMapper.updateWithGeometry(station);
        log.info("充电桩已修改 | id: {} | snCode: {}", id, request.getSnCode());
    }

    @Override
    public void deleteStation(Long id) {
        Station station = this.getById(id);
        if (station == null) {
            throw new BusinessException("充电桩不存在");
        }
        
        checkStationIdle(station);

        this.removeById(id);
        log.info("充电桩已删除 | id: {} | snCode: {}", id, station.getSnCode());
    }

    @Override
    public StationVO getStationById(Long id) {
        Station station = this.getById(id);
        if (station == null) {
            throw new BusinessException("充电桩不存在");
        }
        return convertToVO(station);
    }

    // ==================== 私有方法 ====================

    private void checkSnCodeExists(String snCode, Long excludeId) {
        Station exist = this.lambdaQuery()
                .eq(Station::getSnCode, snCode)
                .ne(excludeId != null, Station::getId, excludeId)
                .one();
        if (exist != null) {
            throw new BusinessException("设备编号已存在");
        }
    }

    private void checkStationIdle(Station station) {
        if (!StationStatusEnum.IDLE.getCode().equals(station.getStatus())) {
            throw new BusinessException("只有空闲状态的充电桩才能操作，当前状态: " + StationStatusEnum.getDesc(station.getStatus()));
        }
    }

    private String buildLocation(Double longitude, Double latitude) {
        return String.format("POINT(%s %s)", longitude, latitude);
    }

    private StationListVO convertToListVO(Station station) {
        StationListVO vo = new StationListVO();
        vo.setId(String.valueOf(station.getId()));
        vo.setSnCode(station.getSnCode());
        vo.setStatus(station.getStatus());
        vo.setStatusDesc(StationStatusEnum.getDesc(station.getStatus()));
        vo.setAddress(station.getAddress());
        vo.setDistance(station.getDistance());
        return vo;
    }

    private StationVO convertToVO(Station station) {
        StationVO vo = new StationVO();
        vo.setId(String.valueOf(station.getId()));
        vo.setSnCode(station.getSnCode());
        vo.setStatus(station.getStatus());
        vo.setStatusDesc(StationStatusEnum.getDesc(station.getStatus()));
        vo.setAddress(station.getAddress());
        vo.setLongitude(station.getLongitude());
        vo.setLatitude(station.getLatitude());
        vo.setCreateTime(station.getCreateTime());
        vo.setUpdateTime(station.getUpdateTime());
        return vo;
    }
}
