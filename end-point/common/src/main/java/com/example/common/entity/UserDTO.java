package com.example.common.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserDTO {
    private Long id;
    private String username;
    private String email;
    private String phone;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
