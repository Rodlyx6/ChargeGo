package com.example.station.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.station.entity.Station;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

public interface StationMapper extends BaseMapper<Station> {

    /**
     * 自定义插入方法，使用 ST_GeomFromText 处理 GEOMETRY
     */
    @Insert("INSERT INTO station (id, sn_code, status, address, location, create_time, update_time) " +
            "VALUES (#{id}, #{snCode}, #{status}, #{address}, ST_GeomFromText(#{location}), #{createTime}, #{updateTime})")
    int insertWithGeometry(Station station);

    /**
     * 自定义更新方法，使用 ST_GeomFromText 处理 GEOMETRY
     */
    @Update("UPDATE station SET sn_code = #{snCode}, status = #{status}, address = #{address}, " +
            "location = ST_GeomFromText(#{location}), update_time = #{updateTime} WHERE id = #{id}")
    int updateWithGeometry(Station station);

    /**
     * 基于 MySQL 空间函数查询附近充电桩
     * ST_Distance_Sphere 计算球面距离（单位：米）
     * ST_AsText 将 GEOMETRY 转为可读的 WKT 格式
     */
    @Select("SELECT id, sn_code, status, address, " +
            "ST_AsText(location) AS location, " +
            "ROUND(ST_Distance_Sphere(location, ST_GeomFromText(CONCAT('POINT(', #{longitude}, ' ', #{latitude}, ')'))), 1) AS distance " +
            "FROM station " +
            "WHERE ST_Distance_Sphere(location, ST_GeomFromText(CONCAT('POINT(', #{longitude}, ' ', #{latitude}, ')'))) <= #{radiusMeters} " +
            "ORDER BY distance ASC")
    List<Station> findNearbyStations(@Param("longitude") Double longitude,
                                      @Param("latitude") Double latitude,
                                      @Param("radiusMeters") Double radiusMeters);

    /**
     * 查询所有充电桩，并将 GEOMETRY 转为可读格式
     */
    @Select("SELECT id, sn_code, status, address, ST_AsText(location) AS location, create_time, update_time FROM station")
    List<Station> selectAllWithLocationText();
}
