package com.example.station.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.station.dto.NearbyStationVO;
import com.example.station.entity.Station;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface StationMapper extends BaseMapper<Station> {

    /**
     * 基于 MySQL 空间函数查询附近充电桩
     * ST_Distance_Sphere 计算球面距离（单位：米），精度足够日常使用
     * ST_GeomFromText 将经纬度字符串转为 POINT 类型
     */
    @Select("SELECT id, sn_code, status, address, " +
            "ROUND(ST_Distance_Sphere(location, ST_GeomFromText(CONCAT('POINT(', #{longitude}, ' ', #{latitude}, ')'))), 1) AS distance " +
            "FROM station " +
            "WHERE ST_Distance_Sphere(location, ST_GeomFromText(CONCAT('POINT(', #{longitude}, ' ', #{latitude}, ')'))) <= #{radiusMeters} " +
            "ORDER BY distance ASC")
    List<NearbyStationVO> findNearbyStations(@Param("longitude") Double longitude,
                                             @Param("latitude") Double latitude,
                                             @Param("radiusMeters") Double radiusMeters);
}
