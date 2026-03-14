package com.example.station.controller;

import com.example.common.result.R;
import com.example.station.model.dto.StationCreateDTO;
import com.example.station.model.dto.StationUpdateDTO;
import com.example.station.model.query.NearbyStationQuery;
import com.example.station.model.vo.StationListVO;
import com.example.station.model.vo.StationVO;
import com.example.station.service.StationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 充电桩控制器（统一）
 * 整合了普通用户和管理员的所有充电桩相关接口
 */
@Slf4j
@RestController
public class StationController {

    @Autowired
    private StationService stationService;

    // ==================== 普通用户接口 ====================

    /**
     * 查询附近充电桩
     */
    @PostMapping("/station/nearby")
    public R getNearbyStations(@RequestBody NearbyStationQuery query) {
        log.info("📍 查询附近充电桩 | 经度: {} | 纬度: {} | 半径: {}米", 
                query.getLongitude(), query.getLatitude(), query.getRadiusMeters());
        
        List<StationListVO> stations = stationService.getNearbyStations(
                query.getLongitude(),
                query.getLatitude(),
                query.getRadiusMeters()
        );
        
        return R.ok("查询成功", stations);
    }

    /**
     * 修改充电桩状态（供 order-service 通过 Feign 调用）
     */
    @PutMapping("/station/status/{stationId}")
    public R updateStationStatus(@PathVariable Long stationId, @RequestParam Integer status) {
        log.info("🔄 更新充电桩状态 | stationId: {} | status: {}", stationId, status);
        
        stationService.updateStationStatus(stationId, status);
        
        return R.ok("状态更新成功", null);
    }

    // ==================== 管理员接口 ====================

    /**
     * 查询所有充电桩
     */
    @GetMapping("/admin/station/list")
    public R listAllStations(@RequestHeader("X-User-Id") Long userId) {
        log.info("📋 管理员查询所有充电桩 | userId: {}", userId);
        
        List<StationVO> stations = stationService.listAllStations();
        
        return R.ok("查询成功", stations);
    }

    /**
     * 新增充电桩
     */
    @PostMapping("/admin/station/add")
    public R addStation(@RequestHeader("X-User-Id") Long userId,
                        @RequestBody StationCreateDTO request) {
        log.info("➕ 管理员新增充电桩 | userId: {} | snCode: {}", userId, request.getSnCode());
        
        stationService.addStation(request);
        
        return R.ok("新增成功", null);
    }

    /**
     * 修改充电桩
     */
    @PutMapping("/admin/station/update/{id}")
    public R updateStation(@RequestHeader("X-User-Id") Long userId,
                           @PathVariable Long id,
                           @RequestBody StationUpdateDTO request) {
        log.info("✏️ 管理员修改充电桩 | userId: {} | id: {} | snCode: {}", userId, id, request.getSnCode());
        
        stationService.updateStation(id, request);
        
        return R.ok("修改成功", null);
    }

    /**
     * 删除充电桩
     */
    @DeleteMapping("/admin/station/delete/{id}")
    public R deleteStation(@RequestHeader("X-User-Id") Long userId,
                           @PathVariable Long id) {
        log.info("🗑️ 管理员删除充电桩 | userId: {} | id: {}", userId, id);
        
        stationService.deleteStation(id);
        
        return R.ok("删除成功", null);
    }

    /**
     * 查询单个充电桩
     */
    @GetMapping("/admin/station/{id}")
    public R getStation(@RequestHeader("X-User-Id") Long userId,
                        @PathVariable Long id) {
        log.info("🔍 管理员查询充电桩详情 | userId: {} | id: {}", userId, id);
        
        StationVO station = stationService.getStationById(id);
        
        return R.ok("查询成功", station);
    }

    /**
     * 测试接口
     */
    @GetMapping("/station/hello")
    public String hello() {
        return "hello station-service";
    }
}
