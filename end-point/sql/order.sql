CREATE DATABASE IF NOT EXISTS `charging_order` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `charging_order`;

CREATE TABLE `order_info` (
                              `id` BIGINT NOT NULL COMMENT '主键ID',
                              `order_no` VARCHAR(64) NOT NULL COMMENT '订单号',
                              `user_id` BIGINT NOT NULL COMMENT '谁下的单',
                              `station_id` BIGINT NOT NULL COMMENT '预约的哪个桩',
                              `status` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '状态: 0待支付 1充电中 2已完成 3已取消',
                              `pay_time` DATETIME DEFAULT NULL COMMENT '支付时间',
                              `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '下单时间',
                              `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                              PRIMARY KEY (`id`),
                              UNIQUE KEY `uk_order_no` (`order_no`),
                              KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单表';