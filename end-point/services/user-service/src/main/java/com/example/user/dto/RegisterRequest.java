package com.example.user.dto;

import lombok.Data;

/**
 * 注册请求参数
 */
@Data
public class RegisterRequest {
    private String phone;
    private String password;
    private String nickname;  // 可选
}
