package com.example.user.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.common.constant.UserRole;
import com.example.common.exception.BusinessException;
import com.example.common.util.JwtUtil;
import com.example.user.dto.LoginResponse;
import com.example.user.entity.User;
import com.example.user.mapper.UserMapper;
import com.example.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public void register(String phone, String password, String nickname) {
        checkPhoneExists(phone);
        
        User user = buildUser(phone, password, nickname);
        this.save(user);
        
        log.info("用户注册成功 | phone: {}", phone);
    }

    @Override
    public LoginResponse login(String phone, String password) {
        User user = getUserByPhone(phone);
        checkPassword(user, password);
        
        String token = JwtUtil.generateToken(user.getId(), user.getRole());
        cacheUserInfo(user);
        
        log.info("用户登录成功 | phone: {} | userId: {} | role: {}", phone, user.getId(), user.getRole());
        
        return buildLoginResponse(user, token);
    }

    /**
     * 检查手机号是否已存在
     */
    private void checkPhoneExists(String phone) {
        User exist = this.lambdaQuery().eq(User::getPhone, phone).one();
        if (exist != null) {
            throw new BusinessException("手机号已被注册");
        }
    }

    /**
     * 根据手机号查询用户
     */
    private User getUserByPhone(String phone) {
        User user = this.lambdaQuery().eq(User::getPhone, phone).one();
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        return user;
    }

    /**
     * 校验密码
     */
    private void checkPassword(User user, String password) {
        if (!DigestUtil.md5Hex(password).equals(user.getPassword())) {
            throw new BusinessException("密码错误");
        }
    }

    /**
     * 缓存用户信息到 Redis
     */
    private void cacheUserInfo(User user) {
        user.setPassword(null);
        redisTemplate.opsForValue().set("login:user:" + user.getId(), user, 7, TimeUnit.DAYS);
    }

    /**
     * 构建用户实体
     */
    private User buildUser(String phone, String password, String nickname) {
        User user = new User();
        user.setPhone(phone);
        user.setPassword(DigestUtil.md5Hex(password));
        user.setNickname(StrUtil.isBlank(nickname) ? "用户" + phone.substring(7) : nickname);
        user.setRole(UserRole.USER);
        return user;
    }

    /**
     * 构建登录响应
     */
    private LoginResponse buildLoginResponse(User user, String token) {
        LoginResponse response = new LoginResponse();
        response.setToken(token);
        response.setUserId(user.getId());
        response.setPhone(user.getPhone());
        response.setNickname(user.getNickname());
        response.setRole(user.getRole());
        return response;
    }
}
