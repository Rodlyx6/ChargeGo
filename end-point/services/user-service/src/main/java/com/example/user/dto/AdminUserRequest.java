package com.example.user.dto;

import lombok.Data;

/**
 * 管理员用户请求 DTO
 */
@Data
public class AdminUserRequest {
    
    /** 手机号 */
    private String phone;
    
    /** 密码 */
    private String password;
    
    /** 昵称 */
    private String nickname;
    
    /** 角色：0普通用户 1管理员 */
    private Integer role;
}
