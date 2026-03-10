package com.example.user.dto;

import lombok.Data;

/**
 * 登录响应结果
 * 直接包含用户信息字段，避免引入外部 DTO
 */
@Data
public class LoginResponse {
    private String token;
    private Long userId;
    private String phone;
    private String nickname;
    private Integer role;  // 0-普通用户 1-管理员
}
