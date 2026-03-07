CREATE DATABASE IF NOT EXISTS `charging_station` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `charging_station`;

CREATE TABLE `station` (
                           `id` BIGINT NOT NULL COMMENT '主键ID',
                           `sn_code` VARCHAR(50) NOT NULL COMMENT '设备编号',
                           `status` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '状态: 0空闲 1预约中 2充电中 3故障',
                           `address` VARCHAR(255) NOT NULL COMMENT '详细地址',
                           `location` POINT NOT NULL COMMENT '经纬度',
                           `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                           `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                           PRIMARY KEY (`id`),
                           UNIQUE KEY `uk_sn_code` (`sn_code`),
                           SPATIAL KEY `sp_location` (`location`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='充电桩表';

INSERT INTO `station` (`id`, `sn_code`, `status`, `address`, `location`)
VALUES
    (101, 'SN-A001', 0, '科技园A栋地下车库', ST_GeomFromText('POINT(113.9431 22.5411)')),
    (102, 'SN-B002', 0, '科技园B栋露天停车场', ST_GeomFromText('POINT(113.9455 22.5433)'));