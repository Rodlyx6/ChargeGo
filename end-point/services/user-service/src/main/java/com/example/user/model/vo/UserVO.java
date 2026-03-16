package com.example.user.model.vo;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 用户信息 VO（不含密码）
 * 用于返回给前端的用户数据
 */
@Data
public class UserVO {
    private String id;
    private String phone;
    private String nickname;
    private Integer role;
    private String roleDesc;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
