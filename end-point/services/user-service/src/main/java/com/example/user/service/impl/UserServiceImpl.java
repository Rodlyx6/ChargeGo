package com.example.user.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.common.entity.UserDTO;
import com.example.common.util.JwtUtil;
import com.example.user.dto.LoginResponse;
import com.example.user.entity.User;
import com.example.user.mapper.UserMapper;
import com.example.user.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public void register(String phone, String password, String nickname) {
        // 1. 校验手机号是否已存在
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getPhone, phone);
        User existUser = this.getOne(queryWrapper);
        
        if (existUser != null) {
            throw new RuntimeException("手机号已被注册");
        }

        // 2. MD5 加密密码
        String encryptedPassword = DigestUtil.md5Hex(password);

        // 3. 创建用户对象并保存
        User user = new User();
        user.setPhone(phone);
        user.setPassword(encryptedPassword);
        user.setNickname(StrUtil.isBlank(nickname) ? "用户" + phone.substring(7) : nickname);
        user.setRole(0);  // 默认普通用户

        this.save(user);
    }

    @Override
    public LoginResponse login(String phone, String password) {
        // 1. 根据手机号查询用户
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getPhone, phone);
        User user = this.getOne(queryWrapper);

        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        // 2. 比对密码（MD5 加密后比对）
        String encryptedPassword = DigestUtil.md5Hex(password);
        if (!encryptedPassword.equals(user.getPassword())) {
            throw new RuntimeException("密码错误");
        }

        // 3. 生成 JWT Token
        String token = JwtUtil.generateToken(user.getId(), user.getRole());

        // 4. 将用户信息存入 Redis（7天过期）
        UserDTO userDTO = new UserDTO();
        BeanUtils.copyProperties(user, userDTO);
        
        String redisKey = "login:user:" + user.getId();
        redisTemplate.opsForValue().set(redisKey, userDTO, 7, TimeUnit.DAYS);

        // 5. 返回登录响应
        LoginResponse response = new LoginResponse();
        response.setToken(token);
        response.setUserInfo(userDTO);

        return response;
    }
}
