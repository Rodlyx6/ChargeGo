package com.example.station.controller;

import com.example.common.result.R;
import com.example.station.dto.NearbySearchRequest;
import com.example.station.dto.NearbyStationVO;
import com.example.station.dto.StationRequest;
import com.example.station.entity.Station;
import com.example.station.service.StationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/station")
public class StationController {

    @Autowired
    private StationService stationService;

    /**
     * 查询附近充电桩
     * 前端传来当前位置经纬度和搜索半径
     */
    @PostMapping("/nearby")
    public R getNearbyStations(@RequestBody NearbySearchRequest request) {
        try {
            List<NearbyStationVO> stations = stationService.getNearbyStations(
                    request.getLongitude(),
                    request.getLatitude(),
                    request.getRadiusMeters()
            );
            return R.ok("查询成功", stations);
        } catch (Exception e) {
            return R.error(500, e.getMessage());
        }
    }

    /**
     * 修改充电桩状态（供 order-service 通过 Feign 调用）
     */
    @PutMapping("/status/{stationId}")
    public R updateStationStatus(@PathVariable Long stationId, @RequestParam Integer status) {
        try {
            stationService.updateStationStatus(stationId, status);
            return R.ok("状态更新成功", null);
        } catch (Exception e) {
            return R.error(500, e.getMessage());
        }
    }

    /**
     * ==================== 管理员接口（需要 role=1）====================
     */

    /**
     * 查询所有充电桩（管理员）
     */
    @GetMapping("/admin/list")
    public R listAllStations() {
        try {
            List<Station> stations = stationService.list();
            return R.ok("查询成功", stations);
        } catch (Exception e) {
            return R.error(500, e.getMessage());
        }
    }

    /**
     * 新增充电桩（管理员）
     */
    @PostMapping("/admin/add")
    public R addStation(@RequestBody StationRequest request) {
        try {
            Station station = new Station();
            station.setSnCode(request.getSnCode());
            station.setAddress(request.getAddress());
            station.setLocation("POINT(" + request.getLongitude() + " " + request.getLatitude() + ")");
            station.setStatus(0);  // 默认空闲
            stationService.save(station);
            log.info("充电桩已新增 | snCode: {} | address: {}", request.getSnCode(), request.getAddress());
            return R.ok("新增成功", station.getId());
        } catch (Exception e) {
            return R.error(500, e.getMessage());
        }
    }

    /**
     * 修改充电桩（管理员）
     */
    @PutMapping("/admin/update/{id}")
    public R updateStation(@PathVariable Long id, @RequestBody StationRequest request) {
        try {
            Station station = stationService.getById(id);
            if (station == null) {
                return R.error(404, "充电桩不存在");
            }
            station.setSnCode(request.getSnCode());
            station.setAddress(request.getAddress());
            station.setLocation("POINT(" + request.getLongitude() + " " + request.getLatitude() + ")");
            stationService.updateById(station);
            log.info("充电桩已修改 | id: {} | snCode: {}", id, request.getSnCode());
            return R.ok("修改成功", null);
        } catch (Exception e) {
            return R.error(500, e.getMessage());
        }
    }

    /**
     * 删除充电桩（管理员）
     */
    @DeleteMapping("/admin/delete/{id}")
    public R deleteStation(@PathVariable Long id) {
        try {
            Station station = stationService.getById(id);
            if (station == null) {
                return R.error(404, "充电桩不存在");
            }
            stationService.removeById(id);
            log.info("充电桩已删除 | id: {} | snCode: {}", id, station.getSnCode());
            return R.ok("删除成功", null);
        } catch (Exception e) {
            return R.error(500, e.getMessage());
        }
    }

    /**
     * 测试接口
     */
    @GetMapping("/hello")
    public String hello() {
        return "hello station-service";
    }
}
