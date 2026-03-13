package com.example.order.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * RabbitMQ 死信队列配置
 *
 * 流程：
 * 下单 → 发消息到【普通队列】（TTL=15分钟）
 *      → 15分钟后消息过期，自动转发到【死信队列】
 *      → 消费者监听死信队列，检查订单状态，未支付则取消
 */
@Configuration
public class RabbitMQConfig {

    // 普通交换机和队列（接收下单消息，设置 TTL）
    public static final String ORDER_EXCHANGE      = "order.exchange";
    public static final String ORDER_QUEUE         = "order.queue";
    public static final String ORDER_ROUTING_KEY   = "order.create";

    // 死信交换机和队列（TTL 到期后转入，触发超时取消）
    public static final String ORDER_DLX_EXCHANGE    = "order.dlx.exchange";
    public static final String ORDER_DLX_QUEUE       = "order.dlx.queue";
    public static final String ORDER_DLX_ROUTING_KEY = "order.timeout";

    // 订单超时时间：15分钟（毫秒）
    public static final int ORDER_TTL_MS = 15 * 60 * 1000;

    // ==================== 死信交换机 + 死信队列 ====================

    @Bean
    public DirectExchange orderDlxExchange() {
        return new DirectExchange(ORDER_DLX_EXCHANGE);
    }

    @Bean
    public Queue orderDlxQueue() {
        return new Queue(ORDER_DLX_QUEUE, true);
    }

    @Bean
    public Binding orderDlxBinding() {
        return BindingBuilder.bind(orderDlxQueue())
                .to(orderDlxExchange())
                .with(ORDER_DLX_ROUTING_KEY);
    }

    // ==================== 普通交换机 + 普通队列（绑定死信参数）====================

    @Bean
    public DirectExchange orderExchange() {
        return new DirectExchange(ORDER_EXCHANGE);
    }

    @Bean
    public Queue orderQueue() {
        Map<String, Object> args = new HashMap<>();
        // 消息过期后转发到死信交换机
        args.put("x-dead-letter-exchange", ORDER_DLX_EXCHANGE);
        args.put("x-dead-letter-routing-key", ORDER_DLX_ROUTING_KEY);
        // 队列级别 TTL（15分钟）
        args.put("x-message-ttl", ORDER_TTL_MS);
        return new Queue(ORDER_QUEUE, true, false, false, args);
    }

    @Bean
    public Binding orderBinding() {
        return BindingBuilder.bind(orderQueue())
                .to(orderExchange())
                .with(ORDER_ROUTING_KEY);
    }
}
