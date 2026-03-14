package com.example.common.enums;

import lombok.Getter;

/**
 * 用户角色枚举
 */
@Getter
public enum UserRoleEnum {
    
    /** 普通用户 */
    USER(0, "普通用户"),
    
    /** 管理员 */
    ADMIN(1, "管理员");
    
    private final Integer code;
    private final String desc;
    
    UserRoleEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }
    
    /**
     * 根据 code 获取枚举
     */
    public static UserRoleEnum getByCode(Integer code) {
        if (code == null) return null;
        for (UserRoleEnum role : values()) {
            if (role.getCode().equals(code)) {
                return role;
            }
        }
        return null;
    }
    
    /**
     * 判断是否为管理员
     */
    public static boolean isAdmin(Integer code) {
        return ADMIN.getCode().equals(code);
    }
}
