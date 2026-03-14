package com.example.user.model.vo;

import lombok.Data;

/**
 * 用户登录响应 VO
 */
@Data
public class UserLoginVO {
    private String token;
    private String userId;
    private String phone;
    private String nickname;
    private Integer role;  // 0-普通用户 1-管理员
}
