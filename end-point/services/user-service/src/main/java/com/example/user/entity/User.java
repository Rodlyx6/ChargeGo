package com.example.user.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("user")
public class User {
    @TableId(value = "id", type= IdType.ASSIGN_ID)
    private Long id;
    @TableField("phone")
    private String phone;
    @TableField("password")
    private String password;
    @TableField("nickname")
    private String nickname;
    /**
     * 角色：0-普通用户 1-管理员等 (TINYINT(1))
     */
    @TableField("role")
    private Integer role;
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
