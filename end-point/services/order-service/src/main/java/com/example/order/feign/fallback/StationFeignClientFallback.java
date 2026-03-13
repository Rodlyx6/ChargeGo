package com.example.order.feign.fallback;

import com.example.common.result.R;
import com.example.order.feign.StationFeignClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * StationFeignClient 降级处理
 * station-service 不可用时执行兜底逻辑
 */
@Slf4j
@Component
public class StationFeignClientFallback implements StationFeignClient {

    @Override
    public R updateStationStatus(Long stationId, Integer status) {
        log.error("station-service 不可用，充电桩状态更新失败 | stationId: {} | status: {}", stationId, status);
        return R.error(500, "station-service 不可用");
    }
}
