package com.example.station.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.common.constant.StationStatus;
import com.example.common.exception.BusinessException;
import com.example.station.dto.StationRequest;
import com.example.station.entity.Station;
import com.example.station.mapper.StationMapper;
import com.example.station.service.AdminStationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class AdminStationServiceImpl extends ServiceImpl<StationMapper, Station> implements AdminStationService {

    @Autowired
    private StationMapper stationMapper;

    @Override
    public List<Station> listAllStations() {
        return stationMapper.selectAllWithLocationText();
    }

    @Override
    public void addStation(StationRequest request) {
        checkSnCodeExists(request.getSnCode(), null);

        Station station = buildStation(request);
        station.setStatus(StationStatus.IDLE);
        station.setCreateTime(LocalDateTime.now());
        station.setUpdateTime(LocalDateTime.now());
        
        stationMapper.insertWithGeometry(station);
        log.info("充电桩已新增 | snCode: {}", request.getSnCode());
    }

    @Override
    public void updateStation(Long id, StationRequest request) {
        Station station = getStationById(id);
        checkStationIdle(station);
        checkSnCodeExists(request.getSnCode(), id);

        station.setSnCode(request.getSnCode());
        station.setAddress(request.getAddress());
        station.setLocation(buildLocation(request));
        station.setUpdateTime(LocalDateTime.now());
        
        stationMapper.updateWithGeometry(station);
        log.info("充电桩已修改 | id: {} | snCode: {}", id, request.getSnCode());
    }

    @Override
    public void deleteStation(Long id) {
        Station station = getStationById(id);
        checkStationIdle(station);
        
        this.removeById(id);
        log.info("充电桩已删除 | id: {} | snCode: {}", id, station.getSnCode());
    }

    @Override
    public Station getStationById(Long id) {
        Station station = this.getById(id);
        if (station == null) {
            throw new BusinessException("充电桩不存在");
        }
        return station;
    }

    /**
     * 检查设备编号是否已存在
     */
    private void checkSnCodeExists(String snCode, Long excludeId) {
        Station exist = this.lambdaQuery()
                .eq(Station::getSnCode, snCode)
                .ne(excludeId != null, Station::getId, excludeId)
                .one();
        if (exist != null) {
            throw new BusinessException("设备编号已存在");
        }
    }

    /**
     * 检查充电桩是否空闲
     */
    private void checkStationIdle(Station station) {
        if (!StationStatus.IDLE.equals(station.getStatus())) {
            throw new BusinessException("只有空闲状态的充电桩才能操作，当前状态: " + StationStatus.getName(station.getStatus()));
        }
    }

    /**
     * 构建充电桩实体
     */
    private Station buildStation(StationRequest request) {
        Station station = new Station();
        station.setSnCode(request.getSnCode());
        station.setAddress(request.getAddress());
        station.setLocation(buildLocation(request));
        return station;
    }

    /**
     * 构建 WKT 格式的 location
     */
    private String buildLocation(StationRequest request) {
        return String.format("POINT(%s %s)", request.getLongitude(), request.getLatitude());
    }
}
