package com.example.order.listener;

import com.example.order.service.OrderCancelService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 订单超时取消监听器
 * 监听死信队列，15分钟后自动取消未支付的订单
 */
@Slf4j
@Component
public class OrderTimeoutListener {

    @Autowired
    private OrderCancelService orderCancelService;

    /**
     * 监听死信队列中的消息
     * 当消息从普通队列转入死信队列时（TTL过期），触发此方法
     */
    @RabbitListener(queues = "order.dlx.queue")
    public void handleTimeoutOrder(String orderNo) {
        log.info("收到超时取消消息 | orderNo: {}", orderNo);
        try {
            orderCancelService.cancelTimeoutOrder(orderNo);
        } catch (Exception e) {
            log.error("处理超时取消失败 | orderNo: {} | error: {}", orderNo, e.getMessage());
        }
    }
}
