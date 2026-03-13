package com.example.order.feign;

import com.example.common.result.R;
import com.example.order.feign.fallback.StationFeignClientFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 调用 station-service 的 Feign 客户端
 */
@FeignClient(value = "station-service", fallback = StationFeignClientFallback.class)
public interface StationFeignClient {

    /**
     * 修改充电桩状态
     * @param stationId 充电桩ID
     * @param status    目标状态（0空闲 1预约中 2充电中 3故障）
     * @return 返回 R 对象
     */
    @PutMapping("/station/status/{stationId}")
    R updateStationStatus(@PathVariable("stationId") Long stationId,
                          @RequestParam("status") Integer status);
}
