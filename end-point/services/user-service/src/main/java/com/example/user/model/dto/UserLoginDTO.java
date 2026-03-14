package com.example.user.model.dto;

import lombok.Data;

/**
 * 用户登录 DTO
 */
@Data
public class UserLoginDTO {
    private String phone;
    private String password;
}
