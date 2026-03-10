package com.example.common.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserDTO {
    private Long id;
    private String phone;
    private String nickname;
    private Integer role;  // 0-普通用户 1-管理员
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    // 注意：不包含 password，登录后返回给前端时不能暴露密码
}
