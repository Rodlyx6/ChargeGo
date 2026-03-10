package com.example.station.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.station.dto.NearbyStationVO;
import com.example.station.entity.Station;
import com.example.station.mapper.StationMapper;
import com.example.station.service.StationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class StationServiceImpl extends ServiceImpl<StationMapper, Station> implements StationService {

    @Autowired
    private StationMapper stationMapper;

    @Override
    public List<NearbyStationVO> getNearbyStations(Double longitude, Double latitude, Double radiusMeters) {
        log.info("查询附近充电桩 | 经度: {} | 纬度: {} | 半径: {}米", longitude, latitude, radiusMeters);
        List<NearbyStationVO> stations = stationMapper.findNearbyStations(longitude, latitude, radiusMeters);
        log.info("查询结果: 共找到 {} 个充电桩", stations.size());
        return stations;
    }

    @Override
    public void updateStationStatus(Long stationId, Integer status) {
        Station station = this.getById(stationId);
        if (station == null) {
            throw new RuntimeException("充电桩不存在，ID: " + stationId);
        }
        station.setStatus(status);
        this.updateById(station);
        log.info("充电桩状态更新 | stationId: {} | 新状态: {}", stationId, status);
    }
}
