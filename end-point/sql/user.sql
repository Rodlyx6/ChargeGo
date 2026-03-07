CREATE DATABASE IF NOT EXISTS `charging_user` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `charging_user`;

CREATE TABLE `user` (
                        `id` BIGINT NOT NULL COMMENT '主键ID',
                        `phone` VARCHAR(20) NOT NULL COMMENT '手机号',
                        `password` VARCHAR(100) NOT NULL COMMENT '密码',
                        `nickname` VARCHAR(50) DEFAULT NULL COMMENT '昵称',
                        `role` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '角色：0普通用户, 1管理员',
                        `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '注册时间',
                        `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                        PRIMARY KEY (`id`),
                        UNIQUE KEY `uk_phone` (`phone`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

INSERT INTO `user` (`id`, `phone`, `password`, `nickname`, `role`)
VALUES (1, '13800000000', '123456', '超级管理员', 1);