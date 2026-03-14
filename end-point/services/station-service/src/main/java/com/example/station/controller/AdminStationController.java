package com.example.station.controller;

import com.example.common.result.R;
import com.example.station.dto.StationRequest;
import com.example.station.entity.Station;
import com.example.station.service.AdminStationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 管理员充电桩管理接口
 */
@Slf4j
@RestController
@RequestMapping("/admin/station")
public class AdminStationController {

    @Autowired
    private AdminStationService adminStationService;

    /**
     * 查询所有充电桩
     */
    @GetMapping("/list")
    public R listAllStations(@RequestHeader("X-User-Id") Long userId) {
        List<Station> stations = adminStationService.listAllStations();
        return R.ok("查询成功", stations);
    }

    /**
     * 新增充电桩
     */
    @PostMapping("/add")
    public R addStation(@RequestHeader("X-User-Id") Long userId,
                        @RequestBody StationRequest request) {
        adminStationService.addStation(request);
        return R.ok("新增成功", null);
    }

    /**
     * 修改充电桩
     */
    @PutMapping("/update/{id}")
    public R updateStation(@RequestHeader("X-User-Id") Long userId,
                           @PathVariable Long id,
                           @RequestBody StationRequest request) {
        adminStationService.updateStation(id, request);
        return R.ok("修改成功", null);
    }

    /**
     * 删除充电桩
     */
    @DeleteMapping("/delete/{id}")
    public R deleteStation(@RequestHeader("X-User-Id") Long userId,
                           @PathVariable Long id) {
        adminStationService.deleteStation(id);
        return R.ok("删除成功", null);
    }

    /**
     * 查询单个充电桩
     */
    @GetMapping("/{id}")
    public R getStation(@RequestHeader("X-User-Id") Long userId,
                        @PathVariable Long id) {
        Station station = adminStationService.getStationById(id);
        return R.ok("查询成功", station);
    }
}
