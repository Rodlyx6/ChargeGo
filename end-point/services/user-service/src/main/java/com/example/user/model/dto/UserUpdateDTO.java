package com.example.user.model.dto;

import lombok.Data;

/**
 * 用户修改请求 DTO
 */
@Data
public class UserUpdateDTO {
    
    /** 手机号 */
    private String phone;
    
    /** 密码（可选，不提供则不修改） */
    private String password;
    
    /** 昵称 */
    private String nickname;
    
    /** 角色：0普通用户 1管理员 */
    private Integer role;
}
