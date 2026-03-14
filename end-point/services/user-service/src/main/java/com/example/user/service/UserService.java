package com.example.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.user.entity.User;
import com.example.user.model.vo.UserLoginVO;

public interface UserService extends IService<User> {
    
    /**
     * 用户注册
     * @param phone 手机号
     * @param password 密码（明文）
     * @param nickname 昵称（可选）
     */
    void register(String phone, String password, String nickname);
    
    /**
     * 用户登录
     * @param phone 手机号
     * @param password 密码（明文）
     * @return 登录响应（包含 Token 和用户信息）
     */
    UserLoginVO login(String phone, String password);
}
