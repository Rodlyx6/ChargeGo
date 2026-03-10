package com.example.station.controller;

import com.example.common.result.R;
import com.example.station.dto.NearbySearchRequest;
import com.example.station.dto.NearbyStationVO;
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
     * 测试接口
     */
    @GetMapping("/hello")
    public String hello() {
        return "hello station-service";
    }
}
