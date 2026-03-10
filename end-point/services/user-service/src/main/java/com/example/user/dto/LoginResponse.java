package com.example.user.dto;

import com.example.common.entity.UserDTO;
import lombok.Data;

/**
 * 登录响应结果
 */
@Data
public class LoginResponse {
    private String token;
    private UserDTO userInfo;
}
