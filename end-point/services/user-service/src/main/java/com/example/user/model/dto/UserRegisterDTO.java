package com.example.user.model.dto;

import lombok.Data;

/**
 * 用户注册 DTO
 */
@Data
public class UserRegisterDTO {
    private String phone;
    private String password;
    private String nickname;
}
