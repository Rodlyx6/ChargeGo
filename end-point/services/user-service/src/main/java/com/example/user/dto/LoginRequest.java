package com.example.user.dto;

import lombok.Data;

/**
 * 登录请求参数
 */
@Data
public class LoginRequest {
    private String phone;
    private String password;
}
