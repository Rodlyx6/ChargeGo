package com.example.order.service;

import com.example.order.model.dto.ChargeOrderCompleteDTO;
import com.example.order.model.dto.ChargeOrderCreateDTO;
import com.example.order.model.dto.ChargeOrderPayDTO;
import com.example.order.model.vo.ChargeOrderVO;

/**
 * 充电订单计算服务接口
 * 职责：处理充电订单的计费、支付、完成等逻辑
 */
public interface ChargeOrderService {

    /**
     * 创建充电订单（计算预期金额）
     * @param userId 用户ID
     * @param request 创建请求（包含 chargeType 和 chargeTime）
     * @return 订单号
     */
    String createChargeOrder(Long userId, ChargeOrderCreateDTO request);

    /**
     * 支付充电订单
     * @param userId 用户ID
     * @param request 支付请求（包含 orderNo 和 actualAmount）
     */
    void payChargeOrder(Long userId, ChargeOrderPayDTO request);

    /**
     * 完成充电订单（计算实际金额和退款）
     * @param userId 用户ID
     * @param request 完成请求（包含 orderNo 和 actualChargeTime）
     * @return 充电订单详情（包含退款信息）
     */
    ChargeOrderVO completeChargeOrder(Long userId, ChargeOrderCompleteDTO request);

    /**
     * 查询充电订单详情
     * @param userId 用户ID
     * @param orderNo 订单号
     * @return 充电订单详情
     */
    ChargeOrderVO getChargeOrderDetail(Long userId, String orderNo);

    /**
     * 自动完成超时订单
     * @param orderNo 订单号
     */
    void autoCompleteTimeoutOrder(String orderNo);
}
